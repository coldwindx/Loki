package com.coldwindx.factory;

import com.coldwindx.annotation.RocketProvider;
import com.coldwindx.annotation.RocketProviderConfig;
import com.coldwindx.provider.Provider;
import com.coldwindx.utils.ClassHelper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProviderFactory {

    @Value("${rocket.scan.package.provider}")
    private String scanPackagePath;

    // Rocket MQ 生产者 集合
    private final Map<String, Provider> CACHE = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("ProviderBeanPostProcessor.init({})", scanPackagePath);

        // 1. 构建包扫描器
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RocketProvider.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RocketProviderConfig.class));

        if(StringUtils.isEmpty(scanPackagePath)){
            throw new NoSuchElementException("spring boot config [rocket.scan.package.provider] is null!");
        }

        // 2. 扫描包
        for(BeanDefinition beanDefinition : scanner.findCandidateComponents(scanPackagePath)) {
            String beanClassName = beanDefinition.getBeanClassName();
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            Class<?> clazz = ClassUtils.resolveClassName(Objects.requireNonNull(beanClassName), classLoader);
            Constructor<?> constructor = ClassHelper.getDefaultConstructor(clazz);

            // 3. 检查类
            if(!clazz.isAssignableFrom(Provider.class)){
                log.warn("RocketProvider annotation must be used in class which implements interface Provider!");
                continue;
            }

            // 4. 获取注解
            RocketProviderConfig rocketProviders = clazz.getAnnotation(RocketProviderConfig.class);
            if (rocketProviders == null || rocketProviders.value() == null)
                continue;

            // 5. 构造bean
            for(RocketProvider c : rocketProviders.value()){
                String name = generateProviderKey(c);
                if(CACHE.containsKey(name))
                    continue;
                Provider provider = (Provider) ClassHelper.construct(constructor);
                Objects.requireNonNull(provider).init(c.cluster(), c.topic(), c.group());
                CACHE.put(name, provider);
            }
        }

    }

    @PreDestroy
    public void destroy(){
        for(Map.Entry<String, Provider> entry : CACHE.entrySet()){
            entry.getValue().destroy();
        }
    }

    private String generateProviderKey(RocketProvider provider){
        return provider.name() + ":" + provider.cluster() + ":" + provider.topic() + ":" + provider.group();
    }

}
