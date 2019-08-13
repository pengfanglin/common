package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信配置类
 * @author 彭方林
 * @date 2019/4/2 14:30
 * @version 1.0
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common.wx")
@Component
public class WxProperties {
    /**
     * 第三方用户唯一凭证
     */
    private String appId;
    /**
     * 第三方用户唯一凭证密钥，即appsecret
     */
    private String secret;
}
