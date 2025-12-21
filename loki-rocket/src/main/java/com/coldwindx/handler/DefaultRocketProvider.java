package com.coldwindx.handler;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.annotation.RocketProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RocketProvider(value = {
        @RocketConfig(topic="topic_common_mq"),
        @RocketConfig(topic = "topic_common_mq_2")
})
public class DefaultRocketProvider extends AbstractRocketProvider{
}
