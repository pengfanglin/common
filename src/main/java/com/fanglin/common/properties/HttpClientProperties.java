package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * common公共类配置的httpClient参数配置
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 10:59
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "http")
@Component
public class HttpClientProperties {
    /**
     * 从连接池获取到连接的超时时间
     */
    private int connectionRequestTimeout=5000;
    /**
     * 建立连接的超时时间
     */
    private int connectTimeout=2000;
    /**
     * 客户端和服务进行数据交互的超时时间
     */
    private int socketTimeout=2000;
    /**
     * 最大连接数
     */
    private int maxTotal=200;
    /**
     * 连接池按route配置的最大连接数(每个请求地址的最大连接数，defaultMaxPerRoute*目标地址<=maxTotal)
     */
    private int defaultMaxPerRoute=200;
}
