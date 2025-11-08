package com.coldwindx.loki.endpoint;

import com.coldwindx.loki.utils.AsyncHelper;
import com.coldwindx.loki.store.WebSockSessionStore;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class ChatEndpoint implements WebSocketHandler {

    @Autowired
    private WebSockSessionStore store;
    @Autowired
    private AsyncHelper async;

    @Override
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        log.info("{} >>> connect...", Thread.currentThread().threadId());
        store.add(session);

        Flux<WebSocketMessage> output = session.receive()
                .flatMap(msg->distribute(session, msg))
                .subscribeOn(Schedulers.parallel());
        return session.send(output);
    }


    private Flux<WebSocketMessage> distribute(WebSocketSession session, WebSocketMessage message) {
        log.info("[{}] >>> distribute message: {}", Thread.currentThread().threadId(), message);
        if(message.getType() == WebSocketMessage.Type.TEXT){
            String msg = message.getPayloadAsText();
            async.submit(()->{
                try {
                    Thread.sleep(1000);
                    return "hi: " + msg;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).thenAccept((res)->{
                session.send(Mono.just(session.textMessage(res))).subscribe();
            });
        }
        return Flux.empty();
    }
}

