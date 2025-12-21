package com.coldwindx.loki.config;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ChatEndpointAdapter websock协议升级
 * @return 协议升级
 */
@Slf4j
@Getter
@Component
public class ChatEndpointAdapter implements HandlerAdapter, Ordered {
    private final WebSocketService webSocketService;
    private final int order;

    public ChatEndpointAdapter() {
        this(new HandshakeWebSocketService());
    }

    public ChatEndpointAdapter(WebSocketService webSocketService) {
        this.order = 2;
        Assert.notNull(webSocketService, "'webSocketService' is required");
        this.webSocketService = webSocketService;
    }

    public boolean supports(Object handler) {
        return WebSocketHandler.class.isAssignableFrom(handler.getClass());
    }

    public @Nonnull Mono<HandlerResult> handle(@Nonnull ServerWebExchange exchange, @Nonnull Object handler) {
        WebSocketHandler webSocketHandler = (WebSocketHandler)handler;
        return this.getWebSocketService().handleRequest(exchange, webSocketHandler).then(Mono.empty());
    }
}