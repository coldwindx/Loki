package com.coldwindx.loki.manager;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;
import java.net.http.WebSocket;

@Component
public class LlmManager {

    private WebSocketHandler handler;

    @PostConstruct
    public void init() {
        this.handler = session -> {};
    }

    public WebSocket connect(){
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(URI.create(""), handler);
    }
}
