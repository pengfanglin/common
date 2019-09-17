package com.fanglin.common.core.enums;

/**
 * 字符串参数约束
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/16 22:03
 **/
public interface KeyEnum {
    /**
     * 字符串
     *
     * @return
     */
    String getKey();

    /**
     * 根据枚举值查找对应的枚举类
     *
     * @param enumClass 枚举类
     * @param key       值
     * @param <E>
     * @return
     */
    static <E extends Enum<?> & KeyEnum> E find(Class<E> enumClass, String key) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return null;
    }
}
