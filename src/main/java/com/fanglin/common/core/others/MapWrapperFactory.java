package com.fanglin.common.core.others;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

/**
 * @author 彭方林
 * @version 1.0
 * @date 2019/9/26 22:17
 **/
public class MapWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return object instanceof Map;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MyMapWrapper(metaObject, (Map<String, Object>) object);
    }

    static class MyMapWrapper extends MapWrapper {
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

