package com.coldwindx.loki.support;

import com.coldwindx.loki.annotation.Shared;
import com.coldwindx.loki.annotation.SharedBy;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class SharedBeanImporter implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata,
                                        @Nonnull BeanDefinitionRegistry registry,
                                        @Nonnull BeanNameGenerator importBeanNameGenerator) {
        // 扫描添加有@EnableShared注解的Application类的主package路径
        String packageName = ClassUtils.getPackageName(importingClassMetadata.getClassName());

        // 扫描添加有@Shared注解的类
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Shared.class));
        Set<BeanDefinition> candidates = scanner.findCandidateComponents(packageName);

        for(BeanDefinition candidate : candidates){
            // 检查类中是否存在@SharedBy注解的方法
            String beanClassName = candidate.getBeanClassName();
            if(beanClassName == null || beanClassName.isEmpty())
                continue;
            Class<?> beanClass = ClassUtils.resolveClassName(beanClassName, ClassUtils.getDefaultClassLoader());
            Method[] methods = beanClass.getDeclaredMethods();
            List<Method> sharedByMethods = Arrays.stream(methods)
                    .filter(method -> method.isAnnotationPresent(SharedBy.class)).toList();

            if(sharedByMethods.isEmpty())
                throw new IllegalStateException("Shared bean class " + beanClassName + " must have at least one method annotated with @SharedBy");
            if(sharedByMethods.size() > 1)
                throw new IllegalStateException("Shared bean class " + beanClassName + " must have at most one method annotated with @SharedBy");

            // 检查@SharedBy注解的方法是否符合要求
            Method sharedByMethod = sharedByMethods.getFirst();
            if(!sharedByMethod.getReturnType().equals(String.class))
                throw new IllegalStateException("SharedBy method " + sharedByMethod.getName() + " in class " + beanClassName + " must return type String");
            if(sharedByMethod.getParameterCount() != 0)
                throw new IllegalStateException("SharedBy method " + sharedByMethod.getName() + " in class " + beanClassName + " must have no parameter");
            if(!sharedByMethod.isAccessible())
                throw new IllegalStateException("SharedBy method " + sharedByMethod.getName() + " in class " + beanClassName + " must be public");

            // @SharedBy注解的方法，注入到对应的BeanDefinition中
            candidate.setAttribute("__sharedByMethod__", sharedByMethod);
            ((AbstractBeanDefinition)candidate).setAbstract(true);
        }
    }
}
