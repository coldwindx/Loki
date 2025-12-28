package com.coldwindx.controller;

import com.coldwindx.loki.factory.SpringBeanFactory;
import com.coldwindx.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping(value = "time")
public class TimeController {
    @Autowired
    private TimeService timeService;

    @PostMapping(value = "get")
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timeService.getDate());
    }

    @PostMapping(value = "remove")
    public void remove() {
        SpringBeanFactory.removeSingleton("timeService");
//        timeService = SpringContext.getBean("timeService", TimeService.class);
    }
}
