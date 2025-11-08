package com.coldwindx.loki.store;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地会话存储，线程安全
 */
@Component
public class WebSockSessionStore {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(16);

    public void add(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void remove(WebSocketSession session) {
        sessions.remove(session.getId());
    }
}
