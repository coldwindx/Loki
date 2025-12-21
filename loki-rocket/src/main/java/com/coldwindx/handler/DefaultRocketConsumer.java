package com.coldwindx.handler;

import com.coldwindx.annotation.RocketConfig;
import com.coldwindx.annotation.RocketConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RocketConsumer(value = {
        @RocketConfig(topic="topic_common_mq"),
        @RocketConfig(topic = "topic_common_mq_2")
})
public class DefaultRocketConsumer extends AbstractRocketConsumer {
}
