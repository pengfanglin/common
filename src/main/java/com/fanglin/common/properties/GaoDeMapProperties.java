package com.fanglin.common.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置类
 * @author 彭方林
 * @date 2019/4/2 14:08
 * @version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "gaode")
@Component
public class GaoDeMapProperties {
    /**
     * 应用秘钥
     */
    private String key;
}
