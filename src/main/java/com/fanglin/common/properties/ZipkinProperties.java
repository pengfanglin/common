package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * zipkin配置文件
 *
 * @author fanglin
 * @version 1.0
 * @date 2019/4/2 10:59
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common.zipkin")
@Component
public class ZipkinProperties {
    /**
     * 是否启用
     */
    private boolean enable = false;
    /**
     * 服务名
     */
    private String serviceName = "zipkin";
    /**
     * 服务名
     */
    private List<String> fieldNames = Collections.singletonList("zipkin");
    /**
     * zipkin服务器地址
     */
    private String address;
    /**
     * 连接超时：毫秒
     */
    private long connectTimeout = 3000;
    /**
     * 数据发送超时：毫秒
     */
    private long readTimeout = 5000;
    /**
     * 采样率：0.01-1
     */
    private float samplingRate = 1;
}
