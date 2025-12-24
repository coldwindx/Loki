package com.coldwindx.loki.entity;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

public abstract class WebSocketSender {
    protected WebSocketSession session;

    public WebSocketSender(WebSocketSession session) {
        this.session = session;
    }

    public abstract Flux<WebSocketMessage> asFlux();

    public abstract void send(String message);
    public abstract void error(WebSocketMessage message);
    public abstract void complete(WebSocketSession session);
}
