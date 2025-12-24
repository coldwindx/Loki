package com.coldwindx.loki.endpoint;

import com.coldwindx.loki.annotation.Endpoint;
import com.coldwindx.loki.entity.WebSocketSender;
import com.coldwindx.loki.support.AbstractEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Endpoint(path = "/chat/v1")
public class ChatEndpointV1 extends AbstractEndpoint {

    WebSocketSender sender;

    @Override
    public boolean beforeHandshake(ServerWebExchange exchange) {
        String auth = exchange.getRequest().getQueryParams().getFirst("auth");
        return auth != null && auth.equals("123");
    }

    @Override
    public void onOpen(WebSocketSender sender) {
        this.sender = sender;
    }

    @Override
    public void onMessage(String message) {
        log.info("ChatEndpointV1 onMessage: {}", message);
        sender.send(message);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error in ChatEndpointV2", throwable);
    }
}

