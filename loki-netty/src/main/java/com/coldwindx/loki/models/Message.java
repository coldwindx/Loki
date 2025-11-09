package com.coldwindx.loki.models;

import com.lmax.disruptor.EventFactory;
import lombok.Data;

/**
 * 消息模型
 */
@Data
public class Message {
    private String msg;

    public static class MessageEventFactory implements EventFactory<Message> {
        @Override
        public Message newInstance() {
            return new Message();
        }
    }
}
