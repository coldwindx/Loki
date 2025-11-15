package com.coldwindx.loki.aop;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class WebFilter implements org.springframework.web.server.WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(()-> Mono.deferContextual(context->{
            MDC.put("LOG_ID", context.get("LOG_ID"));
            return Mono.empty();
        }));
        return Mono.fromCallable(()->chain.filter(exchange)).then();
    }
}
