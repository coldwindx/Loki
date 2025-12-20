package com.coldwindx.provider;

public interface Provider {
    void init(String cluster, String topic, String group);
    default void destroy() {}
    void send();
}
