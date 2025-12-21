package com.coldwindx.loki.config;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
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
public class ChatEndpointAdapter implements HandlerAdapter, Ordered, ApplicationContextAware {
    private final WebSocketService webSocketService;
    private final int order;
    private ApplicationContext context;

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

    public @Nonnull Mono<HandlerResult> handle(ServerWebExchange exchange, @Nonnull Object handler) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params = request.getQueryParams();
        String logid = exchange.getAttribute("LOG_ID");
        log.info("log id is {}", logid);

        WebSocketHandler webSocketHandler = (WebSocketHandler)handler;
        return this.getWebSocketService().handleRequest(exchange, webSocketHandler).then(Mono.empty());
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}