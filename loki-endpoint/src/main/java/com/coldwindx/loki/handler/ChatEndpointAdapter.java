package com.coldwindx.loki.handler;

import com.coldwindx.loki.support.AbstractEndpoint;
import lombok.Getter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

/**
 * ChatEndpointAdapter websock协议升级
 * @return 协议升级
 */
@Getter
@Component
public class ChatEndpointAdapter implements HandlerAdapter, Ordered {
    private final WebSocketService webSocketService;

    public ChatEndpointAdapter() {
        this.webSocketService = new HandshakeWebSocketService();
    }

    public boolean supports(Object handler) {
        return AbstractEndpoint.class.isAssignableFrom(handler.getClass());
    }

    public @NonNull Mono<HandlerResult> handle(@NonNull ServerWebExchange exchange, @NonNull Object handler) {
        AbstractEndpoint endpoint = (AbstractEndpoint) handler;

        // 升级前操作
        boolean ok = endpoint.beforeHandshake(exchange);
        if (!ok) return Mono.empty();

        return this.getWebSocketService().handleRequest(exchange, endpoint).then(Mono.empty());
    }

    /**
     * 协议升级优先级 <br>
     * 不加这个的话，请求会走不到这个Adapter
     * @return 协议升级优先级
     */
    @Override
    public int getOrder() { return 2; }
}