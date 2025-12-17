package com.coldwindx.loki.config;

import com.coldwindx.loki.annotation.Shared;
import com.coldwindx.loki.annotation.SharedGroupMethod;
import com.coldwindx.loki.scope.SharedScoper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class SharedScopeConfig {

    @Bean
    public CustomScopeConfigurer customScopeConfigurer(SharedScoper scope) {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.setScopes(Collections.singletonMap("shared", scope));
        return configurer;
    }

    @Component
    public static class SharedGroupMethodProcessor implements BeanPostProcessor{

        @Autowired
        private SharedScoper scoper;

        @Override
        public @Nullable Object postProcessBeforeInitialization(Object bean, @NotNull String beanName) throws BeansException {
            log.info("Post processing before bean {}", beanName);
            Class<?> beanClass = bean.getClass();
            if(!beanClass.isAnnotationPresent(Shared.class))
                return bean;

            List<Method> methods = new ArrayList<>();
            ReflectionUtils.doWithMethods(beanClass, method -> {
                if(method.isAnnotationPresent(SharedGroupMethod.class))
                    methods.add(method);
            });

            Assert.state(!methods.isEmpty(), "Class " + beanClass.getName() + " annotated with @Shared must have at least one method annotated with @SharedGroupMethod");
            Assert.state(1 == methods.size(), "Class " + beanClass.getName() + " annotated with @Shared must have exactly one method annotated with @SharedGroupMethod, but found " + methods.size());

            Method method = methods.getFirst();
            Assert.state(Modifier.isPublic(method.getModifiers()), "Method " + method.getName() + " in class " + beanClass.getName() + " must be public");
            Assert.state(method.getReturnType().equals(String.class), "Method " + method.getName() + " in class " + beanClass.getName() + " must return a String");

            Supplier<String> supplier = () -> {
                try {
                    return (String) method.invoke(beanClass);
                } catch (Exception e) {
                    throw new RuntimeException("Error invoking method " + method.getName() + " in class " + beanClass.getName(), e);
                }
            };
            scoper.register(beanName, supplier);
            return bean;
        }
    }
}
