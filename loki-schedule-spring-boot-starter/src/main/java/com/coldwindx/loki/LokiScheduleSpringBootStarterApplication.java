package com.coldwindx.loki;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.coldwindx.loki.mapper")
public class LokiScheduleSpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LokiScheduleSpringBootStarterApplication.class, args);
    }

}
