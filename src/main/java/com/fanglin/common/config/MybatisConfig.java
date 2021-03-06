package com.fanglin.common.config;

import com.fanglin.common.core.others.MapWrapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * mybatis配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/8 14:42
 **/
@Configuration
@ConditionalOnClass(ObjectWrapperFactory.class)
@Slf4j
public class MybatisConfig {
    /**
     * tkMapper resultType为map时下划线键值转小写驼峰形式插
     */
    @Bean
    @ConditionalOnClass(ConfigurationCustomizer.class)
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        log.debug("mybatis Map转换器配置成功");
        return configuration -> configuration.setObjectWrapperFactory(new MapWrapperFactory());
    }
}
