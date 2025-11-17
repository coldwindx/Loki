package com.coldwindx.loki.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ChatEndpointFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String logid = headers.getFirst("X-Request-Id");
        log.info("logid is {}.", logid);
        exchange.getAttributes().put("LOG_ID", logid);
        return chain.filter(exchange).contextWrite(ctx -> ctx.put("LOG_ID", logid));
    }
}
