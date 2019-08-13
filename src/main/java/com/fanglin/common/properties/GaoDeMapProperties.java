package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置类
 * @author 彭方林
 * @date 2019/4/2 14:08
 * @version 1.0
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common.gaode")
@Component
public class GaoDeMapProperties {
    /**
     * 应用秘钥
     */
    private String key;
}
