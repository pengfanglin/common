package com.fanglin.common.core.enums;


/**
 * 特殊业务异常父类枚举
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/6/28 16:16
 **/
public interface BusinessEnum {

    /**
     * 获取业务异常码
     *
     * @return
     */
    int getCode();

    /**
     * 业务异常信息
     *
     * @return
     */
    String getMessage();

    /**
     * 根据枚举值查找对应的枚举类
     *
     * @param enumClass 枚举类
     * @param code      枚举值
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
