package com.fanglin.common.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * zipkin配置文件
 * @author fanglin
 * @version 1.0
 * @date 2019/4/2 10:59
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "zipkin")
@Component
public class ZipkinProperties {
    /**
     * 服务名
     */
    private String serviceName="zipkin";
    /**
     * zipkin服务器地址
     */
    private String address;
    /**
     * 连接超时：毫秒
     */
    private long connectTimeout=3000;
    /**
     * 数据发送超时：毫秒
     */
    private long readTimeout=5000;
    /**
     * 采样率：0.01-1
     */
    private float samplingRate=1;
}
