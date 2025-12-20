package com.coldwindx.config;

import com.coldwindx.annotation.EnableRocket;
import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.provider.DefaultRocketProvider;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Slf4j
public class RocketImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private final static String DEFAULT_PACKAGE = "com.coldwindx.provider";

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry,
                                        @NonNull BeanNameGenerator importBeanNameGenerator) {
        MultiValueMap<String, Object> attributes = importingClassMetadata.getAllAnnotationAttributes(EnableRocket.class.getName());
        if(attributes == null) return;
        String[] packagesAttributes = (String[]) attributes.get("packages").getFirst();
        List<String> packages = new ArrayList<>();
        packages.add(DEFAULT_PACKAGE);
        Collections.addAll(packages, packagesAttributes);

        // 1. 构建包扫描器
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(com.coldwindx.annotation.RocketProvider.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(DefaultRocketProvider.class));
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

        for(String path : packages){
            if(path == null || path.isEmpty())
                continue;
            for(BeanDefinition definition : scanner.findCandidateComponents(path)){
                String beanClassName = definition.getBeanClassName();
                Class<?> clazz = ClassUtils.resolveClassName(Objects.requireNonNull(beanClassName), classLoader);

                // 4. 获取注解
                com.coldwindx.annotation.RocketProvider rocketProviderAnnotation = clazz.getAnnotation(com.coldwindx.annotation.RocketProvider.class);
                for(RocketConfig config : rocketProviderAnnotation.value()){
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                    builder.addPropertyValue("cluster", config.cluster());
                    builder.addPropertyValue("topic", config.topic());
                    builder.addPropertyValue("group", config.group());

                    BeanDefinition beanDefinition = builder.getBeanDefinition();
                    String name = generateProviderKey(beanClassName, config);
                    registry.registerBeanDefinition(name, beanDefinition);
                }

            }
        }
    }

    private String generateProviderKey(String beanClassName, RocketConfig rocketConfig){
        return beanClassName + ":" + rocketConfig.cluster() + ":" + rocketConfig.topic() + ":" + rocketConfig.group();
    }
}
