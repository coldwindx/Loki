package com.coldwindx.loki.config;

import com.coldwindx.loki.mq.consumer.MessageEventConsumer;
import com.coldwindx.loki.models.Message;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@Configuration
public class DisruptorConfig {
    @Bean("ringBuffer")
    public RingBuffer<Message> ringBuffer() {
        // 消费者线程池
        ThreadFactory factory = Executors.defaultThreadFactory();
        // 事件工厂
        Message.MessageEventFactory messageEventFactory = new Message.MessageEventFactory();
        // 环形队列大小
        int size = 1024 * 256;
        // 单生产者模式
        Disruptor<Message> disruptor = new Disruptor<>(messageEventFactory, size, factory,
                ProducerType.SINGLE, new BlockingWaitStrategy());
        // 绑定多消费者，群发消息
        disruptor.handleEventsWith(new MessageEventConsumer());
        disruptor.handleEventsWith(new MessageEventConsumer());
        // 启动线程池
        disruptor.start();
        return disruptor.getRingBuffer();
    }

    @Bean("ringBuffer2")
    public RingBuffer<Message> ringBuffer2() {
        // 消费者线程池
        ThreadFactory factory = Executors.defaultThreadFactory();
        // 事件工厂
        Message.MessageEventFactory messageEventFactory = new Message.MessageEventFactory();
        // 环形队列大小
        int size = 1024 * 256;
        // 单生产者模式
        Disruptor<Message> disruptor = new Disruptor<>(messageEventFactory, size, factory,
                ProducerType.SINGLE, new BlockingWaitStrategy());
        // 多消费者
        MessageEventConsumer[] consumers = new MessageEventConsumer[3];
        consumers[0] = new MessageEventConsumer();
        consumers[1] = new MessageEventConsumer();
        consumers[2] = new MessageEventConsumer();
        // 消费者池
        EventHandlerGroup<Message> group = disruptor.handleEventsWithWorkerPool(consumers);
        group.handleEventsWith((event, sequence, endOfBatch) -> log.info("[{}] >>> {}", Thread.currentThread().getName(), event.getMsg()));
        // 启动线程池
        return disruptor.start();
    }
}
