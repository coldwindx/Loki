package com.coldwindx.loki.utils;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Component
public class AsyncHelper {
    @Resource(name = "virtualAsyncExecutor")
    private Executor executor;

    public <U> CompletableFuture<U> submit(Supplier<U> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }
}
