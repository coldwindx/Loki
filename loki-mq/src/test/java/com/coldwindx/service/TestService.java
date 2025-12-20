package com.coldwindx.service;

import com.coldwindx.provider.DefaultRocketProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestService implements ApplicationContextAware {

//    @RocketProvider(topic = "topic_common_mq")
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
}
