package com.coldwindx.config;

import com.coldwindx.annotation.RocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class RocketProviderBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public @Nullable Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        log.info("RocketProviderBeanPostProcessor.postProcessAfterInitialization({})", beanName);

        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(RocketConfig.class))
                continue;

            try {
                RocketConfig annotation = field.getAnnotation(RocketConfig.class);
                field.setAccessible(true);
                field.set(bean, context.getBean(generateProviderKey(field.getType().getName(), annotation)));
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("RocketProviderBeanPostProcessor.postProcessAfterInitialization(" + beanName + ") failed!");
            }
        }

        return bean;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private String generateProviderKey(String beanClassName, RocketConfig rocketConfig){
        return beanClassName + ":" + rocketConfig.cluster() + ":" + rocketConfig.topic() + ":" + rocketConfig.group();
    }
}
