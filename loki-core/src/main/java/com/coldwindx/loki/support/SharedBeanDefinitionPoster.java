package com.coldwindx.loki.support;

import com.coldwindx.loki.annotation.Shared;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Slf4j
@Component
public class SharedBeanDefinitionPoster implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(@Nonnull BeanDefinitionRegistry registry) throws BeansException {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if(beanClassName == null) continue;

            Class<?> beanClass = ClassUtils.resolveClassName(beanClassName, ClassUtils.getDefaultClassLoader());
            Shared sharedAnnotation = beanClass.getAnnotation(Shared.class);
            if(sharedAnnotation == null) continue;

            String scope = sharedAnnotation.scope();

            ((AbstractBeanDefinition) beanDefinition).setAbstract(true);
            beanDefinition.setAttribute("__sharedScope__", scope);
        }
    }
}
