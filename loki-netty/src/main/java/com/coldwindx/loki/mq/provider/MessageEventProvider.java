package com.coldwindx.loki.mq.provider;

import com.coldwindx.loki.models.Message;
import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageEventProvider {
    @Autowired
    @Qualifier("ringBuffer2")
    private RingBuffer<Message> ringBuffer;

    public void send(String msg){
        long sequence = ringBuffer.next();
        try{
            Message event = ringBuffer.get(sequence);
            event.setMsg(msg);
        }finally {
            // 发布事件，激活消费，同时将sequence传递给消费者
            // 必须在finally确保调用，否则会卡sequence
            ringBuffer.publish(sequence);
        }
    }

}
