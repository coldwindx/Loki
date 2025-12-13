package com.coldwindx.loki.config;

import com.coldwindx.loki.scope.SharedScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Slf4j
@Configuration
public class SharedScopeConfig {

    @Bean
    public CustomScopeConfigurer customScopeConfigurer(SharedScope scope) {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.setScopes(Collections.singletonMap("shared", scope));
        return configurer;
    }
}
