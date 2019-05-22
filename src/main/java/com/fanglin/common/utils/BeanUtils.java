package com.fanglin.common.utils;

import com.fanglin.common.core.others.ValidateException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 拷贝对象参数
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/9 11:26
 **/
@Slf4j
public class BeanUtils {

    /**
     * 拷贝对象参数
     *
     * @param source 源文件
     * @param target 目标对象
     * @param <T>    返回值类型
     * @return
     */
    public static <T> T copy(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        try {
            T targetObject = target.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, targetObject);
            return targetObject;
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn("参数复制失败,源对象:{} 目标对象:{} 错误信息:{}", source.getClass(), target, e.getMessage());
            throw new ValidateException("参数复制失败");
        }
    }

    public static <T> List<T> copy(List<?> source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            if (source.isEmpty()) {
                return new LinkedList<>();
            }
            List<T> list = new LinkedList<>();
            for (Object object : source) {
                T targetObject = targetClass.newInstance();
                org.springframework.beans.BeanUtils.copyProperties(object, targetObject);
                list.add(targetObject);
            }
            return list;
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn("参数复制失败,源对象:{} 目标对象:{} 错误信息:{}", source.getClass(), targetClass, e.getMessage());
            throw new ValidateException("参数复制失败");
        }
    }

    /**
     * map转换为bean
     *
     * @param source      map
     * @param targetClass 目标对象
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map<String, ?> source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.newInstance();
            if (source.isEmpty()) {
                return target;
            }
            for (Map.Entry<String, ?> entry : source.entrySet()) {
                if (entry.getKey() != null) {
                    Field field = null;
                    try {
                        field = target.getClass().getDeclaredField(entry.getKey());
                    } catch (NoSuchFieldException e) {
                        log.warn("{}对象属性{}不存在:{}", targetClass, entry.getKey(), e.getMessage());
                    }
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(target, entry.getValue());
                    }
                }
            }
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn("参数复制失败,源对象:{} 目标对象:{} 错误信息:{}", source.getClass(), targetClass, e.getMessage());
            throw new ValidateException("参数复制失败");
        }
    }

    /**
     * map转换为bean
     *
     * @param source 源数据
     * @return
     */
    public static Map<String, Object> mapToBean(Object source) {
        if (source == null) {
            return null;
        }
        try {
            Map<String, Object> target = new HashMap<>(source.getClass().getDeclaredFields().length);
            for (Field field : source.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                target.put(field.getName(), field.get(target));
            }
            return target;
        } catch (IllegalAccessException e) {
            log.warn("参数复制失败,源对象:{} 目标对象:{} 错误信息:{}", source.getClass(), e.getMessage());
            throw new ValidateException("参数复制失败");
        }
    }
}
