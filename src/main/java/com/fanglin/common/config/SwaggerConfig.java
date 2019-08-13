package com.fanglin.common.config;

import com.fanglin.common.properties.SwaggerProperties;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/7/22 22:51
 **/
@Configuration
@EnableSwagger2
@ConditionalOnClass({Docket.class, Api.class})
@ConditionalOnProperty(name = "common.swagger.enable", havingValue = "true")
@Slf4j
public class SwaggerConfig {

    @Autowired
    SwaggerProperties swaggerProperties;

    @Bean
    public Docket createRestApi() {
        log.info("swagger配置成功");
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(swaggerProperties.isEnable() ? PathSelectors.any() : PathSelectors.none())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(swaggerProperties.getTitle())
            .version(swaggerProperties.getVersion())
            .description(swaggerProperties.getDescription())
            .build();
    }

}
