package com.coldwindx.loki.context;

/**
 * 组内共享bean注解的组ID，用于根据当前的当前的组ID，从Spring容器管理中获取bean实例
 */
public class SharedScopeContext {

    private static final ThreadLocal<String> SHARED_GROUP_ID = new ThreadLocal<>();

    public static void setSharedGroupId(String groupId) {
        SHARED_GROUP_ID.set(groupId);
    }

    public static String getSharedGroupId() {
        return SHARED_GROUP_ID.get();
    }

     public static void clear() {
        SHARED_GROUP_ID.remove();
    }
}
