package com.fanglin.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 从spring容器中获取代理类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/3 16:40
 **/
@Component
@Slf4j
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        log.info("SpringUtils配置成功");
        SpringUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 通过类型获取对象
     *
     * @param t 对象类型
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(Class<T> t) throws BeansException {
        return applicationContext.getBean(t);
    }
}
