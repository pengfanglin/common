package com.fanglin.common.config;

import com.fanglin.common.core.filter.RequestLogFilter;
import com.fanglin.common.properties.CommonProperties;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FilterConfig {

    /**
     * 打印请求日志
     */
    @Bean
    @ConditionalOnProperty(prefix = "common.log", name = "enable", havingValue = "true")
    public FilterRegistrationBean filterRegister(RequestLogFilter requestLogFilter, CommonProperties commonProperties) {
        CommonProperties.LogProperties.RequestProperties requestProperties = commonProperties.getLog().getRequest();
        CommonProperties.LogProperties.ResponseProperties responseProperties = commonProperties.getLog().getResponse();
        if (requestProperties.isEnable()) {
            log.info("请求参数日志打印开启成功,日志级别:{}", requestProperties.getLevel());
        }
        if (responseProperties.isEnable()) {
            log.info("返回结果日志打印开启成功,日志级别:{}", responseProperties.getLevel());
        }
        FilterRegistrationBean<RequestLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(requestLogFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
