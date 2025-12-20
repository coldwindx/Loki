package com.coldwindx.provider;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.annotation.RocketProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RocketProvider(value = {
        @RocketConfig(topic="topic_common_mq"),
        @RocketConfig(topic = "topic_common_mq_2")
})
public class DefaultRocketProvider {

    @Setter private String cluster;
    @Setter private String topic;
    @Setter private String group;

    @PostConstruct
    public void init(){
        log.info("DefaultRocketProvider.init(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }

    @PreDestroy
    public void destroy() {
        log.info("DefaultRocketProvider.destroy(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }
    public void send(){

    }
}
