package com.fanglin.common.core.token;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 令牌信息
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/11 22:23
 **/
@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {
    /**
     * 授权token
     */
    private String assessToken;
    /**
     * 授权token超时时间(秒)
     */
    private long assessTokenTimeout = 3600 * 24;
    /**
     * 刷新token
     */
    private String refreshToken;
    /**
     * 刷新token超时时间(秒)
     */
    private long refreshTokenTimeout = 3600 * 24 * 30;

    /**
     * 用户授权数据
     */
    private DefaultTokenData data;
}
