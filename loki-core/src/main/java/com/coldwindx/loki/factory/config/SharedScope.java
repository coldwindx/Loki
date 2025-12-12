package com.coldwindx.loki.factory.config;

import com.coldwindx.loki.context.SharedScopeContext;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SharedScope implements Scope {

    private final Map<String, Object> beans = new ConcurrentHashMap<>(16);
    private final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>(16);

    @Autowired
    private DefaultListableBeanFactory factory;

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        String gid = SharedScopeContext.getSharedGroupId();
        String beanName = generateKey(name, gid);

        Optional.ofNullable(gid).orElseThrow(() -> new IllegalStateException("No shared group ID bound to current thread"));
        return beans.computeIfAbsent(beanName, key -> {
                counts.putIfAbsent(gid, new AtomicInteger(0));
                Object bean = objectFactory.getObject();
                factory.registerSingleton(beanName, bean);
                return bean;
        });
    }

    @Override
    public @Nullable Object remove(String name) {
        String gid = SharedScopeContext.getSharedGroupId();
        if(gid == null) return null;
        return beans.remove(gid);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public @Nullable Object resolveContextualObject(String key) {
        return Scope.super.resolveContextualObject(key);
    }

    @Override
    public @Nullable String getConversationId() {
        return SharedScopeContext.getSharedGroupId();
    }

    private static String generateKey(String name, String gid) {
        return name + ":" + gid;
    }

    /// ===== 自定义方法：用于外部管理生命周期 =====
    public void join(String gid) {
        counts.computeIfAbsent(gid, key -> new AtomicInteger(0)).incrementAndGet();
    }

    public void left(String gid) {
        AtomicInteger count = counts.get(gid);
        if(count == null || count.decrementAndGet() <= 0) {
            Object bean = beans.remove(gid);
            counts.remove(gid);
            factory.destroyBean(bean);
        }
    }
}
