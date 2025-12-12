package com.coldwindx.loki.context;

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
