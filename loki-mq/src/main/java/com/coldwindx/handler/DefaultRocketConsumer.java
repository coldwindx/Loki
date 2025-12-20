package com.coldwindx.handler;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.annotation.RocketConsumer;
import com.coldwindx.entity.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RocketConsumer(value = {
        @RocketConfig(topic="topic_common_mq"),
        @RocketConfig(topic = "topic_common_mq_2")
})
public class DefaultRocketConsumer {

    @Setter private String cluster;
    @Setter private String topic;
    @Setter private String group;
    @Setter private String tags;
    private List<?> consumers = new ArrayList<>(16);

    @PostConstruct
    public void init(){
        log.info("DefaultRocketConsumer.init(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }

    @PreDestroy
    public void destroy() {
        log.info("DefaultRocketConsumer.destroy(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }

    public <T> void recv(Message<T> message){

    }
}
