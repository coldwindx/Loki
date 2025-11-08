package com.coldwindx.loki;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class LokiNettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(LokiNettyApplication.class, args);
    }
}
