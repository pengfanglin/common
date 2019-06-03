package com.fanglin.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * mybatis配置
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/5/8 14:42
 **/
@Configuration
@ConditionalOnClass(ObjectWrapperFactory.class)
@Slf4j
public class MybatisConfig {
    /**
     * tkMapper resultType为map时下划线键值转小写驼峰形式插
     */
    @Bean
    @ConditionalOnClass(tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer.class)
    public tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer tkConfigurationCustomizer() {
        log.info("tkMapper Map转换器加载成功");
        return configuration -> configuration.setObjectWrapperFactory(new MapWrapperFactory());
    }

    /**
     * tkMapper resultType为map时下划线键值转小写驼峰形式插
     */
    @Bean
    @ConditionalOnClass(ConfigurationCustomizer.class)
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        log.info("mybatis Map转换器加载成功");
        return configuration -> configuration.setObjectWrapperFactory(new MapWrapperFactory());
    }

    class MapWrapperFactory implements ObjectWrapperFactory {
        @Override
        public boolean hasWrapperFor(Object object) {
            return object instanceof Map;
        }

        @Override
        public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
            return new MyMapWrapper(metaObject, (Map) object);
        }
    }

    class MyMapWrapper extends MapWrapper {
        MyMapWrapper(MetaObject metaObject, Map<String, Object> map) {
            super(metaObject, map);
        }

        @Override
        public String findProperty(String name, boolean useCamelCaseMapping) {
            if (useCamelCaseMapping) {
                return underlineToCamel(name);
            }
            return name;
        }

        private String underlineToCamel(String param) {
            if (param == null || "".equals(param)) {
                return "";
            }
            String temp = param.toLowerCase();
            int len = temp.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = temp.charAt(i);
                if (c == '_') {
                    if (++i < len) {
                        sb.append(Character.toUpperCase(temp.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
    }
}
