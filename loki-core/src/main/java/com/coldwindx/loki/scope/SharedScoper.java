package com.coldwindx.loki.scope;

import com.coldwindx.loki.context.SharedScopeContext;
import com.coldwindx.loki.factory.SpringBeanFactory;
import com.coldwindx.loki.support.SharedScopeThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 组共享bean具体实现，通过join加入组内，left离开组内；
 * 当引用计数器归0时，自动销毁当前组的bean实例，同时触发@PreDestroy方法
 */
@Slf4j
@Component
public class SharedScoper implements Scope, ApplicationContextAware {

    private static final String PREFIX = "scopedTarget.";

    private ApplicationContext context;
    private final Map<String, Object> beans = new ConcurrentHashMap<>(16);
    private final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>(16);
    private final Map<String, Runnable> destroys = new ConcurrentHashMap<>(16);


    @NotNull
    @Override
    public Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
        // 从spring bean definition中获取bean definition
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        BeanDefinition beanDefinition = registry.getBeanDefinition(name);

        Method sharedByMethod = (Method) beanDefinition.getAttribute("__sharedByMethod__");
        if (sharedByMethod == null)
            throw new IllegalStateException("No @SharedBy method found for bean " + name);

        try {
            String gid = (String) sharedByMethod.invoke(null);
            String beanName = generateKey(name, gid);

            return beans.computeIfAbsent(beanName, key -> {
                counts.putIfAbsent(beanName, new AtomicInteger(1));
                Object bean = objectFactory.getObject();
                SpringBeanFactory.registerSingleton(beanName, bean);
                return bean;
            });

        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error invoking @SharedBy method for bean " + name);
        }
    }

    @Override
    public @Nullable Object remove(@NotNull String name) {
        // 从spring bean definition中获取bean definition
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        BeanDefinition beanDefinition = registry.getBeanDefinition(name);

        Method sharedByMethod = (Method) beanDefinition.getAttribute("__sharedByMethod__");
        if (sharedByMethod == null)
            throw new IllegalStateException("No @SharedBy method found for bean " + name);

        try {
            String gid = (String) sharedByMethod.invoke(null);
            String beanName = generateKey(name, gid);
            counts.remove(beanName);
            return beans.remove(beanName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error invoking @SharedBy method for bean " + name);
        }
    }

    @Override
    public void registerDestructionCallback(@NotNull String name, @NotNull Runnable callback) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        BeanDefinition beanDefinition = registry.getBeanDefinition(name);

        Method sharedByMethod = (Method) beanDefinition.getAttribute("__sharedByMethod__");
        if (sharedByMethod == null)
            throw new IllegalStateException("No @SharedBy method found for bean " + name);

        try {
            String gid = (String) sharedByMethod.invoke(null);
            String beanName = generateKey(name, gid);
            destroys.put(beanName, callback);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Error invoking @SharedBy method for bean " + name);
        }
    }

    @Override
    public @Nullable Object resolveContextualObject(@NotNull String key) {
        log.debug("Resolving context for {}", key);
        return Scope.super.resolveContextualObject(key);
    }

    @Override
    public @Nullable String getConversationId() {
        log.debug("Getting conversation id for {}", SharedScopeContext.getSharedGroupId());
        return SharedScopeContext.getSharedGroupId();
    }

    private static String generateKey(String name, String gid) {
        return name + ":" + gid;
    }

    public void join(Class<?> clazz) {
        String name = Introspector.decapitalize(clazz.getSimpleName());
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        BeanDefinition beanDefinition = registry.getBeanDefinition(PREFIX + name);

        String scope = (String) beanDefinition.getAttribute("__sharedScope__");
        if (scope == null || scope.isEmpty())
            throw new IllegalStateException("No shared scope found for bean " + name);
        if(!SharedScopeThreadLocal.containsKey(scope))
            throw new IllegalStateException("No shared scope found for bean " + name);

        String gid = (String) SharedScopeThreadLocal.get(scope);
        String beanName = PREFIX + generateKey(name, gid);
        counts.computeIfAbsent(beanName, key -> new AtomicInteger(0)).incrementAndGet();
    }

    public void left(Class<?> clazz) {
        String name = Introspector.decapitalize(clazz.getSimpleName());
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        BeanDefinition beanDefinition = registry.getBeanDefinition(PREFIX + name);

        String scope = (String) beanDefinition.getAttribute("__sharedScope__");
        if (scope == null || scope.isEmpty())
            throw new IllegalStateException("No shared scope found for bean " + name);
        if(!SharedScopeThreadLocal.containsKey(scope))
            throw new IllegalStateException("No shared scope found for bean " + name);

        String gid = (String) SharedScopeThreadLocal.get(scope);
        String beanName = PREFIX + generateKey(name, gid);

        AtomicInteger count = counts.get(beanName);
        if(0 < count.decrementAndGet()) return;

        beans.remove(beanName);
        counts.remove(beanName);
        destroys.remove(beanName).run();
        SpringBeanFactory.removeSingleton(beanName);
    }

    // 👇 新增：用于测试的只读查询方法
    public boolean contains(Class<?> clazz, String gid) {
        String name = Introspector.decapitalize(clazz.getSimpleName());
        String beanName = PREFIX + generateKey(name, gid);
        return beans.containsKey(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
