package com.coldwindx.provider;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.annotation.RocketProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RocketProvider(value = {
        @RocketConfig(topic="topic_common_mq")
})
public class DefaultRocketProvider {

    private String cluster;
    private String topic;
    private String group;

    @PostConstruct
    public void init(){
        log.info("DefaultRocketProvider.init(cluster = {}, topic = {}, group = {})", cluster, topic, group);
    }

    public void destroy() {}
    public void send(){

    }
}
