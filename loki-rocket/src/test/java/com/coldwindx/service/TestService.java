package com.coldwindx.service;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.entity.Message;
import com.coldwindx.handler.DefaultRocketProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestService implements ApplicationContextAware {

    @RocketConfig(topic = "topic_common_mq")
    private DefaultRocketProvider provider;
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        context.getBeanNamesForType(DefaultRocketProvider.class);
        log.info("TestService.init()");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public void send(Message<?> message){
        provider.send(message);
    }

    @RocketConfig(topic = "topic_common_mq")
    public void recv(Message<?> message){
        log.info("TestService.recv(message = {})", message);
    }
}
