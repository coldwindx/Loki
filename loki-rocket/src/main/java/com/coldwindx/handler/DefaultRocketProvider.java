package com.coldwindx.handler;

import com.coldwindx.annotation.RocketProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RocketProvider(topic = "topic_common_mq")
@RocketProvider(topic = "topic_common_mq2")
public class DefaultRocketProvider extends AbstractRocketProvider{
}
