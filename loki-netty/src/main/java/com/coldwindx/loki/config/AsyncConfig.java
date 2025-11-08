package com.coldwindx.loki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean(value = "virtualAsyncExecutor")
    public Executor virtualAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
