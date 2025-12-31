package com.coldwindx.loki;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.coldwindx.loki.mapper")
@SpringBootApplication
class LokiMybatisTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(LokiMybatisTestApplication.class, args);
    }
}
