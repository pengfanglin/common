package com.fanglin.common.config;

import com.fanglin.common.properties.HttpClientProperties;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * httpClient配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 13:35
 **/
@Configuration
@ConditionalOnProperty(prefix = "common", name = "http", havingValue = "true")
@ConditionalOnClass(CloseableHttpClient.class)
public class HttpConfig {

    @Autowired
    HttpClientProperties httpClientProperties;

    /**
     * http-client配置
     */
    @Bean("httpClient")
    @Primary
    public CloseableHttpClient httpClient() {
        //指定http-client请求参数
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", SSLConnectionSocketFactory.getSocketFactory())
            .build();
        PoolingHttpClientConnectionManager gcm = new PoolingHttpClientConnectionManager(registry);
        gcm.setMaxTotal(httpClientProperties.getMaxTotal());
        gcm.setDefaultMaxPerRoute(httpClientProperties.getDefaultMaxPerRoute());
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout())
            .setConnectTimeout(httpClientProperties.getConnectTimeout())
            .setSocketTimeout(httpClientProperties.getSocketTimeout())
            .build();
        return HttpClients.custom().setConnectionManager(gcm).setDefaultRequestConfig(requestConfig).build();
    }
}
