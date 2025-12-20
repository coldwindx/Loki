//package com.coldwindx.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.jspecify.annotations.Nullable;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.stereotype.Component;
//import org.jspecify.annotations.NonNull;
//
//@Slf4j
//@Component
//public class ProviderBeanPostProcessor implements BeanPostProcessor {
//    @Override
//    public @Nullable Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
//        log.info("ProviderBeanPostProcessor.postProcessBeforeInitialization({})", beanName);
//        return bean;
//    }
//
//    @Override
//    public @Nullable Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
//        log.info("ProviderBeanPostProcessor.postProcessAfterInitialization({})", beanName);
//
//        return bean;
//    }
//}
