package com.coldwindx.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class AgentService {
    private final ChatClient client;

    public AgentService(ChatClient.Builder builder, ToolCallbackProvider tools) {
        String prompt = """
                  你作为穿衣助手Agent，请严格按以下步骤为用户推荐穿衣搭配：
                  ### 步骤1：获取当前日期
                  当前系统日期为{currentDate}。如果用户输入中包含明确的日期，则使用用户提供的日期，否则使用系统日期。

                  ### 步骤2：校验用户输入
                  如果用户输入不包含城市名则提示用户'请输入城市名', 如果用户输入不包含日期则提示用户'请指定日期'。

                  ### 步骤3：根据城市名和日期获取天气信息
                  调用MCP Server工具'根据城市名获取天气信息'获取天气信息，入参：城市名cityName、日期date(yyyy-mm-dd格式)从用户输入中提取。

                  ### 步骤4：根据天气信息获取穿衣建议
                  调用MCP Server工具'根据天气信息获取穿衣建议'获取穿衣建议并输出，入参：weatherInfo，从上一步获取的天气信息的输出赋值weatherInfo。
                """;
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        prompt = prompt.replace("{currentDate}", date);
        this.client = builder.defaultSystem(prompt).defaultToolCallbacks(tools).build();
    }

    public String call(String input){
        return client.prompt().user(input).call().content();
    }
}
