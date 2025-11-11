package com.coldwindx.loki.store;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsSenderStore {

    private final ConcurrentHashMap<String, Sender> senders = new ConcurrentHashMap<>();

    public Sender get(String id) {
        return senders.get(id);
    }

    public void put(String id, Sender sender) {
        senders.put(id, sender);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {
        private WebSocketSession session;
        private FluxSink<WebSocketMessage> sink;

        public void send(String msg){
            sink.next(session.textMessage(msg));
        }

        public void complete(){
            sink.complete();
        }
    }

}
