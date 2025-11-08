package com.coldwindx.loki.config;

import com.coldwindx.loki.endpoint.ChatEndpoint;
import com.coldwindx.loki.endpoint.support.ChatEndpointAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class NettyConfig {

    @Autowired
    private ChatEndpoint endpoint;

    @Bean
    public HandlerMapping handlerMapping() {
        return new SimpleUrlHandlerMapping(Map.of("/chat", endpoint), 0);
    }

    /**
     * ChatEndpointAdapter websock协议升级
     * @return 协议升级
     */
    @Bean
    public HandlerAdapter chatEndpointAdapter() {
        return new ChatEndpointAdapter();
    }
}
