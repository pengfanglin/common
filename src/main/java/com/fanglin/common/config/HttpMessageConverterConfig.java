package com.fanglin.common.config;

import com.fanglin.common.core.others.AjaxSerializerModifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;

/**
 * mvc序列化配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 17:54
 **/
@Configuration
@ConditionalOnClass({MappingJackson2HttpMessageConverter.class, ObjectMapper.class})
@Slf4j
public class HttpMessageConverterConfig {
    /**
     * 配置springMvc的默认序列化规则，对null对象做特殊处理
     */
    @Bean
    @ConditionalOnProperty(name = "common.mvc-converter", havingValue = "true", matchIfMissing = true)
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter(AjaxSerializerModifier ajaxSerializerModifier) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        log.info("mvc序列化配置成功，时间序列化格式为:{}", dateFormat);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //获取springMvc默认的objectMapper
        ObjectMapper objectMapper = converter.getObjectMapper();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        objectMapper.setDateFormat(sdf);
        // 为mapper注册一个带有SerializerModifier的Factory，针对值为null的字段进行特殊处理
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(ajaxSerializerModifier));
        return converter;
    }
}
