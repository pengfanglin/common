package com.fanglin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/16 15:37
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /**
     * 缓存对象的key，不允许重复
     */
    String value() default "";

    /**
     * 失效时间，默认永不失效
     */
    long timeout() default -1;

    /**
     * 是否允许缓存null值，默认可以缓存空值
     */
    boolean cacheNull() default true;

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
