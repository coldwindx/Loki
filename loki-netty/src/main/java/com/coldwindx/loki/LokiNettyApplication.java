package com.coldwindx.loki;

import com.coldwindx.loki.annotation.EnableEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEndpoint(packages = "com.coldwindx.loki.endpoint")
@SpringBootApplication
@RequiredArgsConstructor
public class LokiNettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(LokiNettyApplication.class, args);
    }
}
