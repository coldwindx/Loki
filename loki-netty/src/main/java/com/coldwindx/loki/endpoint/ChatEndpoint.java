package com.coldwindx.loki.endpoint;

import com.coldwindx.loki.store.WsSenderStore;
import com.coldwindx.loki.store.WebSockSessionHolder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ChatEndpoint implements WebSocketHandler {

    @Autowired
    private WebSockSessionHolder store;

    @Autowired
    private WsSenderStore senders;

    @Override
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        log.info("{} >>> {} connected", Thread.currentThread().threadId(), session.getId());
        store.add(session);

        // 分离接收流和响应流
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(msg->"hi: " + msg)
                .then();
        Mono<Void> output = session.send(Flux.create(sink->senders.put(session.getId(), new WsSenderStore.Sender(session, sink))));
        return Flux.zip(input, output).then();
    }
}

