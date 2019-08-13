package com.fanglin.common.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 极光推送配置类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:08
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common.swagger")
@Component
public class SwaggerProperties {
    /**
     * 标题
     */
    private String title;
    /**
     * 介绍
     */
    private String description;
    /**
     * 版本号
     */
    private String version;

    /**
     * 是否开启
     */
    private boolean enable = false;
}
