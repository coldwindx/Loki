package com.coldwindx.loki.endpoint;

import com.coldwindx.loki.annotation.Endpoint;
import com.coldwindx.loki.store.WebSockSessionHolder;
import com.coldwindx.loki.store.WsSenderStore;
import com.coldwindx.loki.utils.LogHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Endpoint("/chat")
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

        log.info("logid is {}.", session.getHandshakeInfo().getAttributes().get("LOG_ID"));

        // 分离接收流和响应流
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .publishOn(Schedulers.single())
                .map(msg->"hi: " + msg)
                .handle((msg, sink)-> LogHelper.withContext(sink.contextView(), ()->log.info("logid is {}, msg: {}", MDC.get("LOG_ID"), msg)))
                .doOnError(ex->{log.error("exception!", ex);})
                .then();

        Mono<Void> output = session.send(Flux.create(sink->senders.put(session.getId(), new WsSenderStore.Sender(session, sink))));
        return Flux.zip(input, output)
                .doOnError(ex->{log.error("exception!", ex);})
                .then();
    }
}

