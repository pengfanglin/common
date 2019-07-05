package com.fanglin.common.config;

import com.fanglin.common.properties.CrossProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域处理
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 16:36
 **/
@Configuration
@ConditionalOnClass({CorsFilter.class})
@Slf4j
public class CrossConfig {

    @Autowired
    CrossProperties crossProperties;

    /**
     * 跨域处理
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        log.info("跨域支持过滤器配置成功,参数:{}", crossProperties);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //允许cookie跨域
        config.setAllowCredentials(crossProperties.isCredentials());
        // 允许向该服务器提交请求的URI，*表示全部允许。。这里尽量限制来源域，比如http://xxxx:8080 ,以降低安全风险。。
        crossProperties.getAllowedOrigin().forEach(config::addAllowedOrigin);
        // 允许访问的头信息,*表示全部
        crossProperties.getAllowedHeader().forEach(config::addAllowedHeader);
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(18000L);
        // 允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等
        crossProperties.getAllowedMethod().forEach(config::addAllowedMethod);
        //允许返回的请求头
        crossProperties.getExposedHeader().forEach(config::addExposedHeader);
        //允许跨域的请求
        crossProperties.getPath().forEach(item -> {
            source.registerCorsConfiguration(item, config);
        });
        return new CorsFilter(source);
    }
}