package com.fanglin.common.core.aop;

import com.fanglin.common.annotation.LocalCache;
import com.fanglin.common.annotation.LocalCacheRemove;
import com.fanglin.common.utils.MemoryUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
    @Getter
    private static Map<String, CacheData> cache = new ConcurrentHashMap<>();

    /**
     * 切入的验证代码
     */
    @Around("@annotation(localCache)")
    public Object localCacheAop(ProceedingJoinPoint point,LocalCache localCache) throws Throwable {
        MethodSignature joinPointObject = (MethodSignature) point.getSignature();
        Method method = joinPointObject.getMethod();
        long timeout = localCache.timeout();
        String key = getCacheKey(method, point.getArgs(), localCache.value());
        CacheData cacheData = cache.get(key);
        //本地缓存为空时或者用户设置了超时时间并且已经超时，需要重新加载数据
        boolean reload = cacheData == null || (cacheData.getOverdueTime() != -1 && cacheData.getOverdueTime() < System.currentTimeMillis());
        if (reload) {
            Object result = point.proceed();
            if (result != null || localCache.cacheNull()) {
                if (timeout != -1) {
                    timeout = parseTime(timeout, localCache.unit());
                }
                cacheData = new CacheData(result, timeout == -1 ? -1 : System.currentTimeMillis() + timeout);
                cache.put(key, cacheData);
            }
        }
        return cacheData == null ? null : cacheData.getData();
    }

    /**
     * 获取缓存的状态信息
     *
     * @param showValue
     * @return
     */
    public static CacheInfo cacheInfo(Boolean showValue) {
        List<KeyInfo> keys = new ArrayList<>(cache.size());
        AtomicLong totalMemory = new AtomicLong();
        cache.forEach((key, value) -> {
            KeyInfo keyInfo = new KeyInfo(key, value.getOverdueTime(), value.getOverdueTime() - System.currentTimeMillis(), null, null);
            if (showValue != null && showValue) {
                keyInfo.setValue(value.getData());
            }
            long memory = MemoryUtils.sizeOfObject(value.getData());
            keyInfo.setMemorySize(MemoryUtils.format(memory));
            totalMemory.addAndGet(memory);
            keys.add(keyInfo);
        });
        return new CacheInfo(keys.size(), MemoryUtils.format(totalMemory.longValue()), keys);
    }

    /**
     * 切入的验证代码
     */
    @AfterReturning("@annotation(localCacheRemove)")
    public void localCacheRemoveAop(JoinPoint point,LocalCacheRemove localCacheRemove) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        String key = getCacheKey(method, point.getArgs(), localCacheRemove.value());
        cache.remove(key);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CacheData {
        private Object data;
        private long overdueTime;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("缓存信息")
    public static class CacheInfo {
        @ApiModelProperty("key数量")
        private int size;
        @ApiModelProperty("内存占用")
        private String memorySize;
        @ApiModelProperty("key信息")
        private List<KeyInfo> keys;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("key信息")
    public static class KeyInfo {
        @ApiModelProperty("key名称")
        private String key;
        @ApiModelProperty("过期时间")
        private long timeout;
        @ApiModelProperty("剩余时间")
        private long ttl;
        @ApiModelProperty("内存占用")
        private String memorySize;
        @ApiModelProperty("key的值")
        private Object value;
    }
}

