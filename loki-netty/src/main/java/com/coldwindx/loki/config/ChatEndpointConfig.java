package com.coldwindx.loki.config;

import com.coldwindx.loki.endpoint.ChatEndpoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
public class ChatEndpointConfig {

    @Autowired
    private ChatEndpoint endpoint;

    @Bean
    public HandlerMapping handlerMapping() {
        return new SimpleUrlHandlerMapping(Map.of("/chat", endpoint), 0);
    }

    /**
     * ChatEndpointAdapter websock协议升级
     * @return 协议升级
     */
    @Slf4j
    @Getter
    @Component
    public static class ChatEndpointAdapter implements HandlerAdapter, Ordered {
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

}
