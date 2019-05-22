package com.fanglin.common.utils;


import lombok.extern.slf4j.Slf4j;

/**
 * 日志工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 16:34
 **/
@Slf4j
public class LogUtils {
    /**
     * 打印普通日志
     */
    public static void info(Object message) {
        log.info(message == null ? null : JsonUtils.objectToJson(message));
    }

    /**
     * 打印错误日志
     */
    public static void error(Object message) {
        log.error(message == null ? null : JsonUtils.objectToJson(message));
    }

    /**
     * 打印debug日志
     */
    public static void debug(Object message) {
        log.debug(message == null ? null : JsonUtils.objectToJson(message));
    }

    /**
     * 打印警告日志
     */
    public static void warn(Object message) {
        log.warn(message == null ? null : JsonUtils.objectToJson(message));
    }
}
