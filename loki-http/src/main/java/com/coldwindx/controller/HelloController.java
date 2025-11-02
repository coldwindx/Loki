package com.coldwindx.controller;

import com.coldwindx.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "hello")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @PostMapping(value = "say")
    public String say() {
        return helloService.say();
    }
}
