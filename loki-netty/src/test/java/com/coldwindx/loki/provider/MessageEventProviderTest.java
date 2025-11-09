package com.coldwindx.loki.provider;

import com.coldwindx.loki.mq.consumer.MessageEventConsumer;
import com.coldwindx.loki.models.Message;
import com.coldwindx.loki.mq.provider.MessageEventProvider;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageEventProviderTest {
    @Autowired
    MessageEventProvider provider;
    @SneakyThrows
    @Test
    void send() {
        for (int i = 0; i < 100; i++) {
            String msg = "Hello World: " + i;
            provider.send(msg);
        }
        System.in.read();
    }

    @SneakyThrows
    @Test
    void send2() {
        for (int i = 0; i < 100; i++) {
            String msg = "Hello World: " + i;
            provider.send(msg);
        }
        System.in.read();
    }
}