package com.coldwindx.loki.models;

import com.coldwindx.loki.annotation.Shared;
import com.coldwindx.loki.annotation.SharedGroupMethod;
import com.coldwindx.loki.context.SharedScopeContext;
import com.coldwindx.loki.scope.SharedScoper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Shared
public class UserService {

    private String uuid =  UUID.randomUUID().toString();

    public User create(){
        return new User(1, "Alice");
    }

    public String id(){
        log.info("id = {}", uuid);
        return uuid;
    }

    @PostConstruct
    public void init(){
        log.info("init...");
    }

    @PreDestroy
    public void destroy(){
        log.info("destroy: {}", uuid);
    }

    @SharedGroupMethod
    public static String group(){
        return SharedScopeContext.getSharedGroupId();
    }
}
