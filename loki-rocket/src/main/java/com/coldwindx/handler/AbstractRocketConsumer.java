package com.coldwindx.handler;

import com.coldwindx.entity.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public abstract class AbstractRocketConsumer {
    @Getter @Setter private String cluster;
    @Getter @Setter private String topic;
    @Getter @Setter private String group;
    @Getter @Setter private String tags;

    private final List<Consumer<Message<?>>> consumers = new ArrayList<>(16);

    protected void handle(Message<?> message){
        consumers.forEach(consumer -> consumer.accept(message));
    }
    public void register(Consumer<Message<?>> consumer){
        consumers.add(consumer);
    }
}
