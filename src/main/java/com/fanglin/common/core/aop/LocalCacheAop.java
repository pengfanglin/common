package com.fanglin.common.core.aop;

import com.fanglin.common.annotation.LocalCache;
import com.fanglin.common.annotation.LocalCacheRemove;
import lombok.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存切面类，首先从缓存中取数据，数据存在返回缓存数据，否则去数据库取
 *
 * @author 方林
 */
@Component
@Aspect()
@ConditionalOnClass(Aspect.class)
public class LocalCacheAop extends CacheAop {
    /**
     * 本地缓存仓库
     */
    private static Map<String, CacheData> cache = new ConcurrentHashMap<>();

    /**
     * LocalCache切入点规则
     */
    @Pointcut(value = "@annotation(com.fanglin.common.annotation.LocalCache)")
    public void pointLocalCache() {

    }

    /**
     * LocalCacheRemove切入点规则
     */
    @Pointcut(value = "@annotation(com.fanglin.common.annotation.LocalCacheRemove)")
    public void pointLocalCacheRemove() {

    }

    /**
     * 切入的验证代码
     */
    @Around(value = "pointLocalCache()")
    public Object localCacheAop(ProceedingJoinPoint point) throws Throwable {
        MethodSignature joinPointObject = (MethodSignature) point.getSignature();
        Method method = joinPointObject.getMethod();
        LocalCache localCache = method.getAnnotation(LocalCache.class);
        long timeout = localCache.timeout();
        String key = getCacheKey(method, point.getArgs(), localCache.value());
        CacheData cacheData = cache.get(key);
        //本地缓存为空时或者用户设置了超时时间并且已经超时，需要重新加载数据
        boolean reload = cacheData == null || (cacheData.getOverdueTime() != -1 && cacheData.getOverdueTime() < System.currentTimeMillis());
        if (reload) {
            Object result = point.proceed();
            if (result != null || localCache.cacheNull()) {
                if (timeout != -1) {
                    switch (localCache.unit()) {
                        case DAYS:
                            timeout = timeout * 24 * 3600 * 1000;
                            break;
                        case HOURS:
                            timeout = timeout * 3600 * 1000;
                            break;
                        case MINUTES:
                            timeout = timeout * 60 * 1000;
                            break;
                        case SECONDS:
                            timeout = timeout * 1000;
                            break;
                        case MILLISECONDS:
                            break;
                        default:
                            throw new RuntimeException("不支持的时间单位");

                    }
                }
                cacheData = new CacheData(result, timeout == -1 ? -1 : System.currentTimeMillis() + timeout);
                cache.put(key, cacheData);
            }
        }
        return cacheData == null ? null : cacheData.getData();
    }

    /**
     * 切入的验证代码
     */
    @AfterReturning(value = "pointLocalCacheRemove()")
    public void localCacheRemoveAop(JoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        LocalCacheRemove localCacheRemove = method.getAnnotation(LocalCacheRemove.class);
        String key = getCacheKey(method, point.getArgs(), localCacheRemove.value());
        cache.remove(key);
    }


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    class CacheData {
        private Object data;
        private long overdueTime;
    }
}

