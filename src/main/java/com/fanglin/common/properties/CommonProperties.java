package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * common包配置信息
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:08
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common")
@Component
public class CommonProperties {
    /**
     * redis自动配置 默认关闭
     */
    private boolean redis = false;
    /**
     * jedis自动配置 默认关闭
     */
    private boolean jedis = false;
    /**
     * zipkin自动配置 默认关闭
     */
    private boolean zipkin = false;
    /**
     * httpClient自动配置 默认开启
     */
    private boolean http = false;
    /**
     * httpClient自动配置 默认开启
     */
    private boolean jackson = true;
    /**
     * httpClient自动配置 默认开启
     */
    private boolean ajaxJackson = true;
    /**
     * 静态文件保存目录
     */
    private String staticDir;
    /**
     * 请求日志
     */
    private RequestLogProperties requestLog;

    @Setter
    @Getter
    public static class RequestLogProperties {
        /**
         * 是否开启请求日志
         */
        private boolean enable = false;
        /**
         * 日志级别
         */
        private String level = "debug";
    }
}
