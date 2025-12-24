package com.coldwindx.loki.endpoint;

import com.coldwindx.loki.annotation.Endpoint;
import com.coldwindx.loki.entity.WebSocketSender;
import com.coldwindx.loki.support.AbstractEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Endpoint(path = "/chat/v2")
public class ChatEndpointV2 extends AbstractEndpoint {

    WebSocketSender sender;

    @Override
    public void onOpen(WebSocketSender sender) {
        this.sender = sender;
    }

    @Override
    public void onMessage(String message) {
        sender.send(message);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error in ChatEndpointV2", throwable);
    }
}

