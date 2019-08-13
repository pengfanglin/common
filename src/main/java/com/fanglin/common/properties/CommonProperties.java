package com.fanglin.common.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * common包配置信息
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/2 14:08
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "common")
@Component
public class CommonProperties {
    /**
     * redis自动配置
     */
    private boolean redis = false;
    /**
     * jackson自动配置
     */
    private boolean jackson = true;
    /**
     * null值处理jackson自动配置
     */
    private boolean ajaxJackson = true;
    /**
     * 静态文件保存目录
     */
    private String staticDir;
    /**
     * 配置springMvc的默认序列化规则，对null对象做特殊处理
     */
    private boolean mvcConverter = true;
    /**
     * 极光推送
     */
    private AuroraPushProperties push = new AuroraPushProperties();
    /**
     * 跨域处理
     */
    private CrossProperties cross = new CrossProperties();
    /**
     * 高的地图
     */
    private GaoDeMapProperties gaoDe = new GaoDeMapProperties();
    /**
     * http客户端
     */
    private HttpClientProperties httpClient = new HttpClientProperties();
    /**
     * jedis
     */
    private JedisProperties jedis = new JedisProperties();
    /**
     * 请求响应日志
     */
    private LogProperties log = new LogProperties();
    /**
     * API文档
     */
    private SwaggerProperties swagger = new SwaggerProperties();
    /**
     * 微信
     */
    private WxProperties wx = new WxProperties();
    /**
     * zipkin链路追踪
     */
    private ZipkinProperties zipkin = new ZipkinProperties();
}
