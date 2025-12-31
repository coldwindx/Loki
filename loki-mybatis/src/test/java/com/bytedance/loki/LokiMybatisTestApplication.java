package com.bytedance.loki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("unit")
@SpringBootApplication
class LokiMybatisTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(LokiMybatisTestApplication.class, args);
    }
}
