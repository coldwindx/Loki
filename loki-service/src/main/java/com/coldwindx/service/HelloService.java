package com.coldwindx.service;

import com.coldwindx.plugin.NativeLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HelloService {

    @Autowired
    private NativeLib nativeLib;

    public String say() {
        log.info("Hello World: {}", nativeLib.add(1, 2));
        return "hello, world!";
    }
}
