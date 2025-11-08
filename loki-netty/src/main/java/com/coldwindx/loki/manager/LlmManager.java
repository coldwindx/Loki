package com.coldwindx.loki.manager;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LlmManager {
    public Flux<String> say(String msg) {
        // 模拟一个返回流
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Flux.just(("Hello World:" + msg).split(" "));
    }
}
