package com.fanglin.common.config;


import com.fanglin.common.properties.JedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 13:04
 **/
@Configuration
@ConditionalOnProperty(name = "common.jedis.enable", havingValue = "true")
@ConditionalOnClass(JedisPool.class)
@Slf4j
public class JedisConfig {
    @Autowired
    JedisProperties jedisProperties;

    @Bean
    public JedisPool redisPoolFactory() {
        log.info("JedisPool配置成功,参数:{}", jedisProperties);
        return new JedisPool(jedisPoolConfig(), jedisProperties.getHost(), jedisProperties.getPort(), jedisProperties.getTimeout(), jedisProperties.getPassword(), jedisProperties.getDatabase());
    }

    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(jedisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(jedisProperties.getMinIdle());
        jedisPoolConfig.setMaxTotal(jedisProperties.getMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(jedisProperties.getMaxWaitMillis());
        return jedisPoolConfig;
    }
}