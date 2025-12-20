package com.coldwindx.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestService {
    @PostConstruct
    public void init() {
        log.info("TestService.init()");
    }
}
