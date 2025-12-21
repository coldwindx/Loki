package com.coldwindx.loki.config;

import com.coldwindx.loki.annotation.EnableEndpoint;
import com.coldwindx.loki.annotation.Endpoint;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EndpointDefinitionImporter implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata,
                                        @Nonnull BeanDefinitionRegistry registry,
                                        @Nonnull BeanNameGenerator importBeanNameGenerator) {
        MultiValueMap<String, Object> attributes = importingClassMetadata.getAllAnnotationAttributes(EnableEndpoint.class.getName());
        if(attributes == null || attributes.isEmpty() || !attributes.containsKey("packages"))
            return;

        String[] packages = (String[]) attributes.get("packages").getFirst();
        if(packages == null || packages.length == 0)
            return;

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Endpoint.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(WebSocketHandler.class));

        Map<String, RuntimeBeanReference> urlMap = new HashMap<>(16);
        for(String path : packages){
            if(StringUtils.isEmpty(path))
                continue;

            for(BeanDefinition definition : scanner.findCandidateComponents(path)){
                String beanClassName = definition.getBeanClassName();
                Class<?> clazz = ClassUtils.resolveClassName(Objects.requireNonNull(beanClassName), ClassUtils.getDefaultClassLoader());
                RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);

                for(String beanName : registry.getBeanDefinitionNames()){
                    String registryBeanClassName = registry.getBeanDefinition(beanName).getBeanClassName();
                    if(StringUtils.isEmpty(registryBeanClassName))
                        continue;

                    Class<?> registryClass = ClassUtils.resolveClassName(Objects.requireNonNull(registryBeanClassName), ClassUtils.getDefaultClassLoader());
                    if(!clazz.equals(registryClass))
                        continue;
                    urlMap.put(mapping.name(), new RuntimeBeanReference(beanName));
                    break;
                }
            }
        }

        BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(SimpleUrlHandlerMapping.class).getBeanDefinition();
        definition.getConstructorArgumentValues().addGenericArgumentValue(urlMap);
        definition.setBeanClassName(HandlerMapping.class.getName());
        String name = importBeanNameGenerator.generateBeanName(definition, registry);
        registry.registerBeanDefinition(name, definition);
    }
}
