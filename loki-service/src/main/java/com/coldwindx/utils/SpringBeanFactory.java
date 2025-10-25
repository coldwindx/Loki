package com.coldwindx.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanFactory implements ApplicationContextAware {
    @Getter
    private volatile static ConfigurableApplicationContext context;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        if(!(applicationContext instanceof ConfigurableApplicationContext))
            throw new RuntimeException("applicationContext is not ConfigurableApplicationContext");
        context = (ConfigurableApplicationContext) applicationContext;
    }

    public static void removeSingleton(Class<?> clazz) {
        String name = clazz.getName();
        ConfigurableBeanFactory factory = context.getBeanFactory();
        if(!(factory instanceof DefaultListableBeanFactory defaultListableBeanFactory))
            return;
        // step1 销毁bean实例
        if(defaultListableBeanFactory.containsSingleton(name))
            defaultListableBeanFactory.destroySingleton(name);
    }

    public static void removeSingleton(String name) {
        ConfigurableBeanFactory factory = context.getBeanFactory();
        if(!(factory instanceof DefaultListableBeanFactory defaultListableBeanFactory))
            return;
        // step1 销毁bean实例
        if(defaultListableBeanFactory.containsSingleton(name))
            defaultListableBeanFactory.destroySingleton(name);
    }
}
