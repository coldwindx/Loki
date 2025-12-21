package com.coldwindx.config;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.entity.Message;
import com.coldwindx.handler.AbstractRocketConsumer;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Rocket消费方法的注入过程，通过在消费方法上添加@RocketConfig实现
 */
@Slf4j
@Component
public class RocketConsumerBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public @Nullable Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        // 1. 获取当前bean的公开方法，检查是否存在@RocketConfig注解
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if(!method.isAnnotationPresent(RocketConfig.class))
                continue;

            try {
                // 2. 根据@RocketConfig注解的参数，从Spring bean容器管理中寻找对应的RocketConsumer
                RocketConfig annotation = method.getAnnotation(RocketConfig.class);
                Map<String, AbstractRocketConsumer> beansOfType = context.getBeansOfType(AbstractRocketConsumer.class);
                for(AbstractRocketConsumer consumer : beansOfType.values()) {

                    if(!consumer.getCluster().equals(annotation.cluster()))
                        continue;
                    if(!consumer.getTopic().equals(annotation.topic()))
                        continue;
                    if(!consumer.getGroup().equals(annotation.group()))
                        continue;

                    if(consumer.getTags() != null && !consumer.getTags().isEmpty()) {
                        List<String> tagsConsumer = Arrays.asList(consumer.getTags().split("\\|"));
                        List<String> tagsAnnotation = Arrays.asList(annotation.tags());
                        if (!CollectionUtils.containsAny(tagsConsumer, tagsAnnotation))
                            continue;
                    }

                    // 3. 执行方法注册到对应消费者的处理列表里
                    method.setAccessible(true);
                    consumer.register((Message<?> message)-> {
                        try {
                            method.invoke(bean, message);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error(e.getMessage(), e);
                            throw new RuntimeException("Rocket Consumer invoke method failed!");
                        }
                    });
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("RocketConsumerBeanPostProcessor.postProcessAfterInitialization(" + beanName + ") failed!");
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
