package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 极光推送配置类
 * @author 彭方林
 * @date 2019/4/2 14:08
 * @version 1.0
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "push")
@Component
public class AuroraPushProperties {
    /**
     * 极光推送appKey
     */
    private String appKey;
    /**
     * 私钥
     */
    private String masterSecret;
    /**
     * ios推送环境 true:测试环境 false:生产环境 默认生产环境
     */
    private boolean production=true;
}
