package com.fanglin.common.core.others;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ajax返回结果
 *
 * @author 彭方林
 * @date 2018年4月2日
 */
@Data
@Accessors(chain = true)
public class Ajax<T> {
    /**
     * 状态码  200:成功  400:失败 202:等待中 401:未授权 403:权限不足
     */
    private int code;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 结果集
     */
    private T data;

    public static Ajax ok() {
        return new Ajax<String>()
            .setCode(200)
            .setData("操作成功");
    }

    public static <T> Ajax<T> ok(T object) {
        return new Ajax<T>()
            .setCode(200)
            .setData(object);
    }

    public static Ajax error() {
        return new Ajax<String>()
            .setCode(400)
            .setError("操作失败");
    }

    public static Ajax error(String error) {
        return new Ajax<String>()
            .setCode(400)
            .setError(error);
    }

    public static Ajax status(int code, String error) {
        return new Ajax<String>()
            .setCode(code)
            .setError(error);
    }

    public static <T> Ajax<T> status(int code, T data) {
        return new Ajax<T>()
            .setCode(code)
            .setData(data);
    }
}
