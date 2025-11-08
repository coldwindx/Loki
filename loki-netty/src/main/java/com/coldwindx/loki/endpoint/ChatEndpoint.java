package com.coldwindx.loki.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

@Slf4j
@Component
public class ChatEndpoint implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("{} >>> connect...", Thread.currentThread().threadId());
        Flux<WebSocketMessage> output = session.receive()
                .handle((msg, sink)->distribute(session, msg, sink));

        return session.send(output);
    }


    private void distribute(WebSocketSession session, WebSocketMessage message, SynchronousSink<WebSocketMessage> sink) {
        if(message.getType() == WebSocketMessage.Type.TEXT){
            String msg = message.getPayloadAsText();
            sink.next(session.textMessage("hi: " + msg));
        }
        if(message.getType() == WebSocketMessage.Type.BINARY){
            String msg = message.getPayloadAsText();
            sink.next(session.textMessage("hi: " + msg));
        }
    }
}

