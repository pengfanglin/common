package com.fanglin.common.core.enums;

/**
 * 实体类枚举实现该接口才能存到数据库
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/16 22:03
 **/
public interface CodeEnum {
    /**
     * 返回存入数据库的值
     * @return
     */
    int getCode();

    /**
     * 根据枚举值查找对应的枚举类
     * @param enumClass 枚举类
     * @param code 枚举值
     * @param <E>
     * @return
     */
    static <E extends Enum<?> & CodeEnum> E find(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code){
                return e;
            }
        }
        return null;
    }
}
