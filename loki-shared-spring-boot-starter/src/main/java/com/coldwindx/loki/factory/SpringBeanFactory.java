package com.coldwindx.loki.factory;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 手动创建&消耗bean
 * ps: 每次创建时，可以触发@PostConstruct，但销毁时无法触发@PreDestroy
 */
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

    /// ===== 添加bean方法：用于在bean容器管理中添加bean实例 =====
    public static void registerSingleton(String name, Object bean) {
        ConfigurableBeanFactory factory = context.getBeanFactory();
        if(!(factory instanceof DefaultListableBeanFactory defaultListableBeanFactory))
            return;
        // step1 注册bean实例
        if(!defaultListableBeanFactory.containsSingleton(name))
            defaultListableBeanFactory.registerSingleton(name, bean);
    }

    public static <T> void registerSingleton(T bean) {
        ConfigurableBeanFactory factory = context.getBeanFactory();
        if(!(factory instanceof DefaultListableBeanFactory defaultListableBeanFactory))
            return;
        // step1 注册bean实例
        String name = bean.getClass().getName();
        if(!defaultListableBeanFactory.containsSingleton(name))
            defaultListableBeanFactory.registerSingleton(name, bean);
    }

    /// ===== 移除bean方法：用于从bean容器管理中移除bean实例 =====
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
