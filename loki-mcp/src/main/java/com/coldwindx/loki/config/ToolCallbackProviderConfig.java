package com.coldwindx.loki.config;

import com.coldwindx.loki.tool.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderConfig {
    /**
     * WeatherService注册为MCP工具
     * @param service
     * @return
     */
    @Bean
    public ToolCallbackProvider weatherTools(WeatherService service) {
        return MethodToolCallbackProvider.builder().toolObjects(service).build();
    }
}
