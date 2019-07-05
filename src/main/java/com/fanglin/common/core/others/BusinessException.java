package com.fanglin.common.core.others;

import com.fanglin.common.core.enums.BusinessEnum;
import lombok.Getter;

/**
 * 用户自定义异常，只打印异常信息，不打印堆栈信息
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 17:57
 **/
public class BusinessException extends RuntimeException {
    @Getter
    private int code;

    public BusinessException(BusinessEnum businessEnum) {
        super(businessEnum.getMessage());
        this.code = businessEnum.getCode();
    }

    public BusinessException(String error) {
        super(error);
        this.code = BusinessEnum.DEFAULT.getCode();
    }
}
