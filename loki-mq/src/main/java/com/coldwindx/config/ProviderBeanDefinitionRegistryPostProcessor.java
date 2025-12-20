//package com.coldwindx.config;
//
//import com.coldwindx.annotation.RocketProvider;
//import com.coldwindx.annotation.RocketProviderConfig;
//import io.micrometer.common.util.StringUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.jspecify.annotations.NonNull;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionBuilder;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
//import org.springframework.core.env.Environment;
//import org.springframework.core.type.filter.AnnotationTypeFilter;
//import org.springframework.core.type.filter.TypeFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ClassUtils;
//
//import java.util.*;
//
//@Slf4j
//@Component
//public class ProviderBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
//
//    private Environment environment;
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
//        log.info("ProviderBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()");
//        // 1. 构建包扫描器
//        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
//        scanner.addIncludeFilter(new AnnotationTypeFilter(RocketProvider.class));
//        scanner.addIncludeFilter(new AnnotationTypeFilter(RocketProviderConfig.class));
//
//        String packageScanPath = environment.getProperty("rocket.scan.package.provider");
//        if(StringUtils.isEmpty(packageScanPath)){
//            throw new NoSuchElementException("spring boot config [rocket.scan.package.provider] is null!");
//        }
//
//        // 2. 扫描包
//        for(BeanDefinition beanDefinition : scanner.findCandidateComponents(packageScanPath)) {
//            String beanClassName = beanDefinition.getBeanClassName();
//            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
//            Class<?> clazz = ClassUtils.resolveClassName(Objects.requireNonNull(beanClassName), classLoader);
//
//            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
//            BeanDefinition providerBeanDefinition = builder.getBeanDefinition();
//
//            // 3. 获取注解，并注入bean配置
//            RocketProviderConfig rocketProviders = clazz.getAnnotation(RocketProviderConfig.class);
//            if(rocketProviders != null && rocketProviders.value() != null){
//                for(RocketProvider c : rocketProviders.value()){
//                    String name = generateNewBeanName(beanClassName, c);
//                    registry.registerBeanDefinition(name, providerBeanDefinition);
//                }
//            }
//
//            RocketProvider rocketProvider = clazz.getAnnotation(RocketProvider.class);
//            if(rocketProvider != null){
//                String name = generateNewBeanName(beanClassName, rocketProvider);
//                registry.registerBeanDefinition(name, providerBeanDefinition);
//            }
//        }
//    }
//
//    @Override
//    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        log.info("ProviderBeanDefinitionRegistryPostProcessor.postProcessBeanFactory()");
//        BeanDefinitionRegistryPostProcessor.super.postProcessBeanFactory(beanFactory);
//    }
//
//    @Override
//    public void setEnvironment(@NonNull Environment environment) {
//        this.environment = environment;
//    }
//
//    private String generateNewBeanName(String beanName, RocketProvider provider){
//        return beanName + ":" + provider.cluster() + ":" + provider.topic() + ":" + provider.group();
//    }
//}
