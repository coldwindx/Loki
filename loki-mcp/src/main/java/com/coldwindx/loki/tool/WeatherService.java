package com.coldwindx.loki.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    @Tool(description = "根据城市名获取天气信息")
    public String weather(String city, String date) {
        return city + date + "的天气是18℃-25℃ 暴雨。";
    }

    @Tool(description = "根据天气信息获取穿衣建议")
    public String dressing(String weather){
        return "冲锋衣 + 靴子";
    }
}
