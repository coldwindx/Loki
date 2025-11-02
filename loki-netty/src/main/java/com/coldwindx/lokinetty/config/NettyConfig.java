package com.coldwindx.lokinetty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "netty.server")
public class NettyConfig {
    private String host;
    private int port;
    private boolean useEpoll;
}
