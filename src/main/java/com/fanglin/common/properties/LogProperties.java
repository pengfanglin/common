package com.fanglin.common.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

/**
 * 请求响应日志
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:08
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common.log")
@Component
public class LogProperties {
    /**
     * 开始日志
     */
    private boolean enable = false;
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
