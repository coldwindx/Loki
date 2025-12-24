package com.coldwindx.loki.entity;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class UnicastWebSocketSender extends WebSocketSender {

    private final Sinks.Many<WebSocketMessage> sink;

    public UnicastWebSocketSender(WebSocketSession session) {
        super(session);
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
    }

     @Override
    public Flux<WebSocketMessage> asFlux() {
        return this.sink.asFlux();
    }

    @Override
    public void send(String message) {
        Sinks.EmitResult result = sink.tryEmitNext(session.textMessage(message));
        if (result.isFailure()) {
            throw new RuntimeException("Failed to emit message: " + result);
        }
    }

    @Override
    public void error(WebSocketMessage message) {

    }

    @Override
    public void complete(WebSocketSession session) {

    }

}
