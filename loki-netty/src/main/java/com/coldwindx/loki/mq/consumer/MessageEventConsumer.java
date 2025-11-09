package com.coldwindx.loki.mq.consumer;

import com.coldwindx.loki.models.Message;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class MessageEventConsumer implements WorkHandler<Message>, EventHandler<Message> {
    @Override
    public void onEvent(Message event, long sequence, boolean endOfBatch) throws Exception {
        // 随机休眠1~3秒
        Random random = new Random();
        int num = random.nextInt(2000) + 1000;
        Thread.sleep(num);
        log.info("[{}] >>> {}", Thread.currentThread().getName(), event);
    }

    @Override
    public void onEvent(Message event) throws Exception {
        // 随机休眠1~3秒
        Random random = new Random();
        int num = random.nextInt(2000) + 1000;
        Thread.sleep(num);
        log.info("[{}] >>> {}", Thread.currentThread().getName(), event);
    }
}
