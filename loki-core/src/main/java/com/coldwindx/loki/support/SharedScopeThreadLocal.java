package com.coldwindx.loki.support;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SharedScopeThreadLocal {
    private static final ThreadLocal<SharedScopeAdapter> SHARED_SCOPE_THREAD_LOCAL = ThreadLocal.withInitial(SharedScopeAdapter::new);

    public static void put(@NotNull String key, @NotNull Object value) {
        SHARED_SCOPE_THREAD_LOCAL.get().put(key, value);
    }

    public static Object get(@NotNull String key) {
        return SHARED_SCOPE_THREAD_LOCAL.get().get(key);
    }

    public static void remove(@NotNull String key) {
        SHARED_SCOPE_THREAD_LOCAL.get().remove(key);
    }

    public static void clear() {
        SHARED_SCOPE_THREAD_LOCAL.remove();
    }

    public static boolean containsKey(@NotNull String key) {
        return SHARED_SCOPE_THREAD_LOCAL.get().containsKey(key);
    }

    protected static class SharedScopeAdapter implements Map<String, Object> {
        private final Map<String, Object> map = new HashMap<>(16);
        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return map.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            return map.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return map.remove(key);
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ?> m) {
            map.putAll(m);
        }

        @Override
        public void clear() {
            map.clear();
        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return map.keySet();
        }

        @NotNull
        @Override
        public Collection<Object> values() {
            return map.values();
        }

        @NotNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return map.entrySet();
        }

    };
}
