package com.coldwindx.loki.store;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地会话存储，线程安全
 */
@Component
public class WebSockSessionHolder {
    private final Map<String, WebSocketSession> store = new ConcurrentHashMap<>(16);

    public void add(WebSocketSession session) {
        store.put(session.getId(), session);
    }

    public void remove(WebSocketSession session) {
        store.remove(session.getId());
    }
}
