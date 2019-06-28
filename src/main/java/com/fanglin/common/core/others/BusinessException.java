package com.fanglin.common.core.others;

import com.fanglin.common.core.enums.BusinessEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/6/28 16:15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(BusinessEnum businessEnum) {
        super(businessEnum.getMessage());
        this.code = businessEnum.getCode();
    }
}
