package com.fanglin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 简单鉴权
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/16 15:38
 **/
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
    /**
     * 业务类型，用于区分不同的账号体系
     * @return
     */
    String value() default "default";
}
