package com.coldwindx.handler;

import com.coldwindx.annotation.RocketConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RocketConsumer(topic="topic_common_mq")
public class DefaultRocketConsumer extends AbstractRocketConsumer {
}
