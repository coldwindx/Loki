package com.coldwindx.handler;

import com.coldwindx.entity.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractRocketProvider {

    @Getter @Setter private String cluster;
    @Getter @Setter private String topic;
    @Getter @Setter private String group;

    public void send(Message<?> message){
        log.info("DefaultRocketProvider.send(cluster = {}, topic = {}, group = {}, message = {})", cluster, topic, group, message);
    }
}
