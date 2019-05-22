package com.fanglin.common.config;

import com.fanglin.common.core.filter.RequestLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * 过滤器配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 13:35
 **/
@Configuration
@ConditionalOnClass({Filter.class, FilterRegistrationBean.class})
public class FilterConfig {

    /**
     * 打印请求日志
     */
    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ConditionalOnProperty(prefix = "common", name = "requestLog", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean filterRegister() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new RequestLogFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
