package com.coldwindx.lokinetty.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ChannelStore {
    // 频道Key
    private final static AttributeKey<Object> CLIENT_ID = AttributeKey.valueOf("clientId");
    // 客户端KEY <--> Channel ID
    private final static ConcurrentHashMap<String, ChannelId> channels = new ConcurrentHashMap<>(16);
    private final static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 单机可重入锁
    private static final Lock lock = new ReentrantLock();

    public static void bind(ChannelHandlerContext ctx, String client) {
        lock.lock();
        try {
            // 找到客户的Channel
            Channel channel = Optional.of(channels.containsKey(client))
                    .filter(Boolean::booleanValue)
                    .map(b->channels.get(client))
                    .map(group::find)
                    .orElse(null);

            // 清除历史Channel
            Optional.ofNullable(channel).ifPresent(o-> channels.remove(client));
            Optional.ofNullable(channel).ifPresent(ChannelOutboundInvoker::close);

            // 绑定新的Channel ID
            ctx.channel().attr(CLIENT_ID).set(client);
            channels.put(client, ctx.channel().id());
            group.add(ctx.channel());
        }finally {
            lock.unlock();
        }
    }
}
