package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
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
     * redis自动配置
     */
    private boolean redis = false;
    /**
     * jedis自动配置
     */
    private boolean jedis = false;
    /**
     * zipkin自动配置
     */
    private boolean zipkin = false;
    /**
     * httpClient自动配置
     */
    private boolean http = false;
    /**
     * jackson自动配置
     */
    private boolean jackson = true;
    /**
     * null值处理jackson自动配置
     */
    private boolean ajaxJackson = true;
    /**
     * 静态文件保存目录
     */
    private String staticDir;
    /**
     * 请求日志
     */
    private LogProperties log = new LogProperties();

    @Setter
    @Getter
    public static class LogProperties {
        /**
         * 开始日志
         */
        private boolean enable=false;
        /**
         * 请求日志
         */
        private RequestProperties request = new RequestProperties();
        /**
         * 响应日志
         */
        private ResponseProperties response = new ResponseProperties();

        @Setter
        @Getter
        public static class RequestProperties {
            /**
             * 是否开启请求日志
             */
            private boolean enable = false;
            /**
             * 日志级别
             */
            private LogLevel level = LogLevel.DEBUG;
        }

        @Setter
        @Getter
        public static class ResponseProperties {
            /**
             * 是否开启请求日志
             */
            private boolean enable = false;
            /**
             * 日志级别
             */
            private LogLevel level = LogLevel.DEBUG;
        }
    }
}
