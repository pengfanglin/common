package com.fanglin.common.core.others;

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
     * 令牌超时时间(毫秒)
     */
    private long timeout;
    /**
     * 用户的主键
     */
    private Integer id;
    /**
     * 生成的令牌
     */
    private String token;
}
