package com.fanglin.common.util;


import com.fanglin.common.core.others.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;


/**
 * hibernate-validator校验工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/17 16:18
 **/
@Component
@ConditionalOnClass(Validator.class)
@Slf4j
public class ValidatorUtils {

    private static Validator validator;

    public ValidatorUtils(Validator validator) {
        log.info("ValidatorUtils配置成功");
        ValidatorUtils.validator = validator;
    }

    public static void validate(Object obj) {
        Set<ConstraintViolation<Object>> results = validator.validate(obj);
        if (!results.isEmpty()) {
            for (ConstraintViolation<Object> result : results) {
                throw new BusinessException(result.getMessage());
            }

        }
    }
}
