package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信配置文件
 *
 * @author fanglin
 * @version 1.0
 * @date 2019/4/2 10:59
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common.sms")
@Component
public class SmsProperties {
    /**
     * 阿里短信
     */
    private AliProperties ali = new AliProperties();
    /**
     * 腾讯短信
     */
    private TengXunProperties tengXun = new TengXunProperties();

    @Setter
    @Getter
    public static class AliProperties {
        /**
         * 授权key,阿里云控制台查看
         */
        private String accessKeyId;
        /**
         * 授权秘钥,阿里云控制台查看
         */
        private String accessSecret;
    }

    @Setter
    @Getter
    public static class TengXunProperties {
        /**
         * 应用id
         */
        private String appid;
        /**
         * 应用秘钥
         */
        private String appKey;
    }
}
