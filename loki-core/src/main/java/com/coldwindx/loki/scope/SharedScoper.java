package com.coldwindx.loki.scope;

import com.coldwindx.loki.context.SharedScopeContext;
import com.coldwindx.loki.factory.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 组共享bean具体实现，通过join加入组内，left离开组内；
 * 当引用计数器归0时，自动销毁当前组的bean实例，同时触发@PreDestroy方法
 */
@Slf4j
@Component
public class SharedScoper implements Scope {

    private static final String PREFIX = "scopedTarget.";

    private final Map<String, Object> beans = new ConcurrentHashMap<>(16);
    private final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>(16);
    private final Map<String, Supplier<String>> groups = new ConcurrentHashMap<>(16);
    private final Map<String, Runnable> destroys = new ConcurrentHashMap<>(16);

    @NotNull
    @Override
    public Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
        log.debug("Getting bean {}", name);
        String gid = SharedScopeContext.getSharedGroupId();
        String beanName = generateKey(name, gid);

        Optional.ofNullable(gid).orElseThrow(() -> new IllegalStateException("No shared group ID bound to current thread"));
        return beans.computeIfAbsent(beanName, key -> {
                counts.putIfAbsent(beanName, new AtomicInteger(1));
                Object bean = objectFactory.getObject();
                SpringBeanFactory.registerSingleton(beanName, bean);
                return bean;
        });
    }

    @Override
    public @Nullable Object remove(@NotNull String name) {
        log.debug("Removing bean {}", name);
        String gid = SharedScopeContext.getSharedGroupId();
        String beanName = generateKey(name, gid);
        counts.remove(beanName);
        return beans.remove(beanName);
    }

    @Override
    public void registerDestructionCallback(@NotNull String name, @NotNull Runnable callback) {
        log.debug("Registering destruction callback {}", name);
        String gid = SharedScopeContext.getSharedGroupId();
        String beanName = generateKey(name, gid);
        destroys.put(beanName, callback);
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

    /// ===== 自定义方法：用于外部管理生命周期 =====
    public void register(String name, Supplier<String> supplier) {
        groups.put(name, supplier);
    }

    public void join(String name, String gid) {
        log.debug("Joining shared group {}", gid);
        String beanName = PREFIX + generateKey(name, gid);
        counts.computeIfAbsent(beanName, key -> new AtomicInteger(0)).incrementAndGet();
    }

    public void join(Class<?> clazz, String gid) {
        log.debug("Joining shared group {}", gid);
        String name = Introspector.decapitalize(clazz.getSimpleName());
        String beanName = PREFIX + generateKey(name, gid);
        counts.computeIfAbsent(beanName, key -> new AtomicInteger(0)).incrementAndGet();
    }

    public void left(String name, String gid) {
        String beanName = PREFIX + generateKey(name, gid);
        AtomicInteger count = counts.get(beanName);
        log.debug("Lefting shared group {} and ref count {}", gid, count.get());
        if(count.decrementAndGet() <= 0) {
            beans.remove(beanName);
            counts.remove(beanName);
            destroys.remove(beanName).run();
            SpringBeanFactory.removeSingleton(beanName);
        }
    }


    public void left(Class<?> clazz, String gid) {
        String name = Introspector.decapitalize(clazz.getSimpleName());
        String beanName = PREFIX + generateKey(name, gid);
        AtomicInteger count = counts.get(beanName);
        log.info("Lefting shared group {} and ref count {}", gid, count.get());
        if(count.decrementAndGet() <= 0) {
            log.info("Delete shared group {}", gid);
            beans.remove(beanName);
            counts.remove(beanName);
            destroys.remove(beanName).run();
            SpringBeanFactory.removeSingleton(beanName);
        }
    }

    // 👇 新增：用于测试的只读查询方法
    public boolean contains(Class<?> clazz, String gid) {
        String name = Introspector.decapitalize(clazz.getSimpleName());
        String beanName = PREFIX + generateKey(name, gid);
        return beans.containsKey(beanName);
    }
}
