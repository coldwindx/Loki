package com.coldwindx.loki.support;

import com.coldwindx.loki.entity.WebSocketSender;

public interface WebSocketReceiver {
    default void onOpen(WebSocketSender sender) {}

    default void onMessage(String body) throws Exception{}
    default void onBinary(byte[] body) throws Exception{}
    default void onPong() throws Exception{}
    default void onPing() throws Exception{}

    default void onError(Throwable e){}
    default void onComplete(){}
    default void onClose(){}
}
