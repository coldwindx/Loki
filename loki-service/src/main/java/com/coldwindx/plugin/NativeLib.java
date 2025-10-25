package com.coldwindx.plugin;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Component
public class NativeLib {
    @PostConstruct
    public void init() {
        URL resource = ClassLoader.getSystemResource("native.dll");
        Optional.ofNullable(resource).orElseThrow(() -> new RuntimeException("native.dll not found"));
        System.load(resource.getPath());
        log.info("Native library loaded");
    }
    public native int add(int a, int b);
}
