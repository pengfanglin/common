package com.fanglin.common.config;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.sampler.CountingSampler;
import com.fanglin.common.properties.ZipkinProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.concurrent.TimeUnit;

/**
 * zipkin链路追踪
 *
 * @author fanglin
 * @version 1.0
 * @date 2019/4/2 10:55
 **/
@Configuration
@ConditionalOnProperty(prefix = "common", name = "zipkin", havingValue = "true")
@ConditionalOnClass({OkHttpSender.class, Tracing.class})
public class ZipkinConfig {

    @Autowired
    private ZipkinProperties zipkinProperties;

    @Bean
    public Tracing tracing() {
        Sender sender = OkHttpSender.create(zipkinProperties.getAddress());
        //使用异步发送，不影响业务性能
        AsyncReporter<Span> reporter = AsyncReporter.builder(sender)
            //连接超时
            .closeTimeout(zipkinProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
            //消息发送超时
            .messageTimeout(zipkinProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
            .build();
        return Tracing.newBuilder()
            .localServiceName(zipkinProperties.getServiceName())
            .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
            //指定采样率 Sampler.ALWAYS_SAMPLE:总是采样 Sampler.NEVER_SAMPLE:总是不采样 CountingSampler指定采样率
            .sampler(CountingSampler.create(zipkinProperties.getSamplingRate()))
            .spanReporter(reporter)
            .build();
    }
}
