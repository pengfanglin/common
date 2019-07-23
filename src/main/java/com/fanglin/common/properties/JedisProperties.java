package com.fanglin.common.properties;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jedis配置类
 * @author 彭方林
 * @date 2019/4/2 14:30
 * @version 1.0
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "jedis")
@Component
public class JedisProperties {
    /**
     * 主机地址
     */
    private String host="127.0.0.1";
    /**
     * 端口
     */
    private int port=6379;
    /**
     * 密码
     */
    private String password;
    /**
     * 数据库
     */
    private int database=0;
    /**
     * 连接超时 单位:毫秒
     */
    private int timeout;
    /**
     * 最小闲置连接数
     */
    private int minIdle=10;
    /**
     * 最大闲置连接数
     */
    private int maxIdle=50;
    /**
     * 最大活动对象数
     */
    private int maxTotal=500;
}
