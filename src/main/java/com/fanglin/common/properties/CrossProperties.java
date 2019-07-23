package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 跨域配置类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:30
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "cross")
@Component
public class CrossProperties {
    /**
     * 是否允许cookie跨域
     */
    private boolean credentials = true;
    /**
     * 允许跨域的域名
     */
    private List<String> allowedOrigin = Collections.singletonList("*");
    /**
     * 允许跨域的请求头
     */
    private List<String> allowedHeader = Collections.singletonList("*");
    /**
     * 预检请求的缓存时间（秒）
     */
    private long maxAge = 3600L;
    /**
     * 允许跨域的方法类型
     */
    private List<String> allowedMethod = Collections.singletonList("*");
    /**
     * 允许返回的请求头
     */
    private List<String> exposedHeader = Collections.singletonList("content-disposition");
    /**
     * 允许跨域的请求
     */
    private List<String> path = Collections.singletonList("/**");
}
