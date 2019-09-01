package com.fanglin.common.core.enums;

import lombok.Getter;

/**
 * @author 彭方林
 * @version 1.0
 * @date 2019/9/1 17:48
 **/
public enum TokenKeyEnum implements KeyEnum {
    /**
     * 鉴权token
     */
    ACCESS_TOKEN("access_token"),
    /**
     * 刷新token
     */
    REFRESH_TOKEN("refresh_token");

    @Getter
    private String key;

    TokenKeyEnum(String key) {
        this.key = key;
    }
}
