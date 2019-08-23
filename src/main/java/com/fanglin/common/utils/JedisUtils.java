package com.fanglin.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * jedis操作
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/14 10:49
 **/
@Component
@ConditionalOnClass(JedisPool.class)
@Slf4j
public class JedisUtils {

    private static JedisPool jedisPool;

    public JedisUtils(@Autowired(required = false) JedisPool jedisPool) {
        if (jedisPool == null) {
            log.warn("未配置JedisPool,JedisUtils不可用");
        } else {
            log.info("JedisUtils配置成功");
        }
        JedisUtils.jedisPool = jedisPool;
    }

    /**
     * 获取jedis连接
     *
     * @return
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static String set(String key, String value, String nxxx, String expx, long timeout) {
        log.debug("{} {} {} {} {}", key, value, nxxx, expx, timeout);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, nxxx, expx, timeout);
        }
    }

    public static String setNx(String key, String value, String expx, long timeout) {
        return set(key, value, "nx", expx, timeout);
    }

    public static String setXx(String key, String value, String expx, long timeout) {
        return set(key, value, "xx", expx, timeout);
    }

    public static String set(String key, String value) {
        log.debug("{} {}", key, value);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public static String set(String key, String value, String expx, long timeout) {
        log.debug("{} {} {} {}", key, value, expx, timeout);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, expx, timeout);
        }
    }

    public static String setNx(String key, String value) {
        log.debug("{} {}", key, value);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, "nx");
        }
    }

    public static String setXx(String key, String value) {
        log.debug("{} {}", key, value);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value, "xx");
        }
    }

    /**
     * 根据key读取
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        log.debug(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    /**
     * 删除key
     *
     * @param key
     * @return
     */
    public static Long del(String key) {
        log.debug(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        }
    }
}
