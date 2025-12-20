package com.coldwindx.handler;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.annotation.RocketConsumer;
import com.coldwindx.entity.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RocketConsumer(value = {
        @RocketConfig(topic="topic_common_mq"),
        @RocketConfig(topic = "topic_common_mq_2")
})
public class DefaultRocketConsumer {

    @Getter @Setter private String cluster;
    @Getter @Setter private String topic;
    @Getter @Setter private String group;
    @Getter @Setter private String tags;

    private final List<Consumer<Message<?>>> consumers = new ArrayList<>(16);

    @PostConstruct
    public void init(){
        log.info("DefaultRocketConsumer.init(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }

    @PreDestroy
    public void destroy() {
        log.info("DefaultRocketConsumer.destroy(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }

    public void recv(Message<?> message){
        consumers.forEach(consumer -> consumer.accept(message));
    }

    public void register(Consumer<Message<?>> consumer){
        consumers.add(consumer);
    }
}
