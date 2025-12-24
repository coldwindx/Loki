package com.coldwindx.loki.support;

import com.coldwindx.loki.entity.UnicastWebSocketSender;
import com.coldwindx.loki.entity.WebSocketSender;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;

public abstract class AbstractEndpoint implements WebSocketReceiver, WebSocketHandler {

    Scheduler scheduler;

    @Override
    public @NonNull Mono<Void> handle(@NonNull WebSocketSession session) {
        this.scheduler = Schedulers.newSingle("endpoint");

        WebSocketSender sender = new UnicastWebSocketSender(session);
        Mono<Void> receive = session.receive().map(this::load).publishOn(scheduler).handle(this::onHandle).then();
        Mono<Void> send = session.send(sender.asFlux()).then();

        this.onOpen(sender);

        return Flux.zip(receive, send)
                .doOnError(this::onError)
                .doOnComplete(this::onComplete)
                .doFinally(this::onFinally)
                .contextCapture().then();
    }

    private EndpointMessage<?> load(WebSocketMessage msg){
        if(msg.getType() == WebSocketMessage.Type.TEXT)
            return new EndpointMessage<>(msg.getType(), msg.getPayloadAsText());
        if(msg.getType() == WebSocketMessage.Type.BINARY){
            int count = msg.getPayload().readableByteCount();
            byte[] bytes = new byte[count];
            msg.getPayload().read(bytes);
            return new EndpointMessage<>(WebSocketMessage.Type.BINARY, bytes);
        }
        if(msg.getType() == WebSocketMessage.Type.PING)
            return new EndpointMessage<>(WebSocketMessage.Type.PONG, null);
        if(msg.getType() == WebSocketMessage.Type.PONG)
            return new EndpointMessage<>(WebSocketMessage.Type.PONG, null);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void onHandle(EndpointMessage<?> msg, SynchronousSink<Object> sink) {
        try{
            switch (msg.getType()) {
                case TEXT: this.onMessage((String) msg.getBody()); break;
                case BINARY: this.onBinary((byte[]) msg.getBody()); break;
                case PING: this.onPing(); break;
                case PONG: this.onPong(); break;
                default: throw new UnsupportedOperationException("Not supported yet.");
            }
        } catch (Exception e) {
            sink.error(e);
        }
    }

    private void onFinally(SignalType signal) {
        this.onClose();
        scheduler.dispose();
    }



    @Data
    @AllArgsConstructor
    protected static class EndpointMessage<T>{
        WebSocketMessage.Type type;
        T body;
    }

}
