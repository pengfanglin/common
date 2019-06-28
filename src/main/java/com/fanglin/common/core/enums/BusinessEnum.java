package com.fanglin.common.core.enums;

import lombok.Getter;

/**
 * 特殊业务异常父类枚举
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/6/28 16:16
 **/
public enum BusinessEnum {
    /**
     * 默认异常
     */
    DEFAULT(0, "异常");

    @Getter
    private int code;

    @Getter
    private String message;

    BusinessEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据枚举值查找对应的枚举类
     *
     * @param enumClass 枚举类
     * @param code      枚举值
     * @param <E>
     * @return
     */
    static BusinessEnum find(Class<? extends BusinessEnum> enumClass, int code) {
        BusinessEnum[] enumConstants = enumClass.getEnumConstants();
        for (BusinessEnum e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
