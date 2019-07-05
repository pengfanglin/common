package com.fanglin.common.core.others;

import java.util.Collection;
import java.util.Map;

/**
 * 校验异常断言
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/24 14:57
 **/
public class Assert {

    public static void notNull(Object object, String errorMsg) {
        if (object == null) {
            throw new BusinessException(errorMsg);
        }
    }


    public static void notEmpty(String text, String errorMsg) {
        if (text == null || text.length() == 0) {
            throw new BusinessException(errorMsg);
        }
    }

    public static void notEmpty(Object[] array, String errorMsg) {
        if (array == null || array.length == 0) {
            throw new BusinessException(errorMsg);
        }
    }

    public static void notEmpty(Collection<?> collection, String errorMsg) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(errorMsg);
        }
    }

    public static void notEmpty(Map map, String errorMsg) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(errorMsg);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }

    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message);
        }
    }
}
