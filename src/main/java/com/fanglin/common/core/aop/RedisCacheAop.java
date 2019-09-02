package com.fanglin.common.core.aop;

import com.fanglin.common.annotation.RedisCache;
import com.fanglin.common.annotation.RedisCacheRemove;
import com.fanglin.common.utils.OthersUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;

/**
 * redis缓存切面类，首先从缓存中取数据，数据存在返回缓存数据，否则去数据库取
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/30 17:40
 **/
@Component
@Aspect()
@ConditionalOnClass({Aspect.class, JedisPool.class})
public class RedisCacheAop extends CacheAop {

    @Autowired(required = false)
    JedisPool jedisPool;

    /**
     * 切入的验证代码
     */
    @Around("@annotation(redisCache)")
    public Object localCacheAop(ProceedingJoinPoint point,RedisCache redisCache) throws Throwable {
        MethodSignature joinPointObject = (MethodSignature) point.getSignature();
        Method method = joinPointObject.getMethod();
        String key = getCacheKey(method, point.getArgs(), redisCache.value());
        long timeout = redisCache.timeout();
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] cacheData = jedis.get(key.getBytes());
            Object data;
            //本地缓存为空时或者用户设置了超时时间并且已经超时，需要重新加载数据
            if (cacheData == null) {
                data = point.proceed();
                if (data != null || redisCache.cacheNull()) {
                    if (timeout != -1) {
                        timeout = parseTime(timeout, redisCache.unit());
                    }
                    jedis.set(key.getBytes(), OthersUtils.objectToByte(data), "px".getBytes(), timeout);
                }
            } else {
                data = OthersUtils.byteToObject(cacheData);
            }
            return data;
        }
    }

    /**
     * 切入的验证代码
     */
    @AfterReturning("@annotation(redisCacheRemove)")
    public void localCacheRemoveAop(JoinPoint point,RedisCacheRemove redisCacheRemove) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        String key = getCacheKey(method, point.getArgs(), redisCacheRemove.value());
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
    }

}

