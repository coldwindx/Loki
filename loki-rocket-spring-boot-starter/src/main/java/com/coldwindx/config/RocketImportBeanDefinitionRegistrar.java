package com.coldwindx.config;

import com.coldwindx.annotation.EnableRocket;
import com.coldwindx.annotation.RocketConsumer;
import com.coldwindx.annotation.RocketProvider;
import com.coldwindx.handler.AbstractRocketConsumer;
import com.coldwindx.handler.AbstractRocketProvider;
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

/**
 * /@EnableRocket 自动 package 扫描，用于 spring 自动注册全部 RocketProvider 和 RocketConsumer
 */
@Slf4j
public class RocketImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    private final static String DEFAULT_PACKAGE = "com.coldwindx.handler";

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry,
                                        @NonNull BeanNameGenerator importBeanNameGenerator) {
        // 1. 获取@EnableRocket的注解信息
        MultiValueMap<String, Object> attributes = importingClassMetadata.getAllAnnotationAttributes(EnableRocket.class.getName());
        if(attributes == null) return;
        String[] packagesAttributes = (String[]) attributes.get("packages").getFirst();
        List<String> packages = new ArrayList<>();
        packages.add(DEFAULT_PACKAGE);
        Collections.addAll(packages, packagesAttributes);

        // 2. 构建包扫描器
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RocketProvider.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RocketConsumer.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(AbstractRocketProvider.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(AbstractRocketConsumer.class));

        // 3. 开启扫描
        for(String path : packages){
            if(path == null || path.isEmpty())
                continue;
            for(BeanDefinition definition : scanner.findCandidateComponents(path)){
                String beanClassName = definition.getBeanClassName();
                Class<?> clazz = ClassUtils.resolveClassName(Objects.requireNonNull(beanClassName), ClassUtils.getDefaultClassLoader());

                // 4.1 注册Rocket生产者
                RocketProvider[] rocketProviderAnnotations = clazz.getAnnotationsByType(RocketProvider.class);
                Arrays.stream(rocketProviderAnnotations).forEach(annotation -> registerBeanDefinition(registry, beanClassName, clazz, annotation));

                // 4.2 注册Rocket消费者
                RocketConsumer[] rocketConsumerAnnotation = clazz.getAnnotationsByType(RocketConsumer.class);
                Arrays.stream(rocketConsumerAnnotation).forEach(annotation -> registerBeanDefinition(registry, beanClassName, clazz, annotation));
            }
        }
    }

    private void registerBeanDefinition(@NonNull BeanDefinitionRegistry registry, String beanClassName, Class<?> clazz, RocketProvider config) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        builder.addPropertyValue("cluster", config.cluster());
        builder.addPropertyValue("topic", config.topic());
        builder.addPropertyValue("group", config.group());

        BeanDefinition beanDefinition = builder.getBeanDefinition();
        String name = generateRocketKey(beanClassName, config);
        registry.registerBeanDefinition(name, beanDefinition);
    }

    private void registerBeanDefinition(@NonNull BeanDefinitionRegistry registry, String beanClassName, Class<?> clazz, RocketConsumer config) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        builder.addPropertyValue("cluster", config.cluster());
        builder.addPropertyValue("topic", config.topic());
        builder.addPropertyValue("group", config.group());
        builder.addPropertyValue("tags", config.tags());

        BeanDefinition beanDefinition = builder.getBeanDefinition();
        String name = generateRocketKey(beanClassName, config);
        registry.registerBeanDefinition(name, beanDefinition);
    }


    private String generateRocketKey(String beanClassName, RocketProvider rocketConfig){
        return beanClassName + ":" + rocketConfig.cluster() + ":" + rocketConfig.topic() + ":" + rocketConfig.group() + ":" + rocketConfig.cluster();
    }

    private String generateRocketKey(String beanClassName, RocketConsumer rocketConfig){
        String tags = String.join("|", rocketConfig.tags());
        return beanClassName + ":" + rocketConfig.cluster() + ":" + rocketConfig.topic() + ":" + rocketConfig.group() + ":" + rocketConfig.cluster() + ":" + tags;
    }
}
