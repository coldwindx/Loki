package com.coldwindx.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware, EnvironmentAware {
    @Getter
    private volatile static ApplicationContext context;
    private volatile static Environment environment;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringContext.context = applicationContext;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        SpringContext.environment = environment;
    }

    /**
     * 根据bean类型获取bean实例
     * @param clazz bean类型
     * @return bean实例
     * @param <T> 类型
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static String getConfig(String key){
        return environment.getProperty(key);
    }

    public static String getConfig(String key, String default_){
        return environment.getProperty(key, default_);
    }

    public static void publish(ApplicationEvent event) {
        context.publishEvent(event);
    }
}
