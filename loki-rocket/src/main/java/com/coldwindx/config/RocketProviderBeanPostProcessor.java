package com.coldwindx.config;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.handler.AbstractRocketProvider;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 为bean容器，自动注入RocketProvider属性
 */
@Slf4j
@Component
public class RocketProviderBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public @Nullable Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        // 1. 获取当前bean的属性
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(RocketConfig.class))
                continue;

            // 2. 从spring bean容器中获取对应的Rocket生产者，注入属性
            try {
                RocketConfig annotation = field.getAnnotation(RocketConfig.class);
                Map<String, AbstractRocketProvider> beansOfType = context.getBeansOfType(AbstractRocketProvider.class);
                for(AbstractRocketProvider provider : beansOfType.values()) {

                    if (!provider.getCluster().equals(annotation.cluster()))
                        continue;
                    if (!provider.getTopic().equals(annotation.topic()))
                        continue;
                    if (!provider.getGroup().equals(annotation.group()))
                        continue;

                    field.setAccessible(true);
                    field.set(bean, provider);
                    break;
                }
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

    private String generateRocketKey(String beanClassName, RocketConfig rocketConfig){
        String tags = String.join("|", rocketConfig.tags());
        return beanClassName + ":" + rocketConfig.cluster() + ":" + rocketConfig.topic() + ":" + rocketConfig.group() + ":" + rocketConfig.cluster() + ":" + tags;
    }
}
