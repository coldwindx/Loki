package com.coldwindx.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class TimeService {
    private Date date;

    @PostConstruct
    public void init() {
        log.info("TimeService: init");
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    @PreDestroy
    public void destroy() {
        log.info("TimeService: destroy");
    }
}
