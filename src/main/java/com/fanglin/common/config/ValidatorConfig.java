package com.fanglin.common.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * hibernate-validator配置类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/17 14:48
 **/
@Configuration
@ConditionalOnClass(HibernateValidator.class)
@Slf4j
public class ValidatorConfig {
    @Bean
    Validator validator() {
        log.info("HibernateValidator参数校验配置成功");
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
