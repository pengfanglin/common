package com.fanglin.common.core.token;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * redis授权数据
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/8/14 11:20
 **/
@Getter
@Setter
@Accessors(chain = true)
public class TokenData implements Serializable {
    /**
     * 用户的主键
     */
    private Integer id;
}
