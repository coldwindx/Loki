package com.coldwindx.loki.endpoint.support;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Getter
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

    public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params = request.getQueryParams();
        log.info("params: {}", params);

        WebSocketHandler webSocketHandler = (WebSocketHandler)handler;
        return this.getWebSocketService().handleRequest(exchange, webSocketHandler).then(Mono.empty());
    }
}
