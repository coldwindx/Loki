package com.coldwindx.lokinetty.server;

import com.coldwindx.lokinetty.config.NettyConfig;
import com.coldwindx.lokinetty.handler.WebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServer {
    private final NettyConfig config;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void start() {
        log.info("=========== Netty Server Start ============");
        bossGroup = config.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = config.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(config.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(config.getHost(), config.getPort()))
                    .childHandler(new ChannelInit())
                    .option(ChannelOption.SO_BACKLOG, 128)          // TCP缓冲区
                    .childOption(ChannelOption.TCP_NODELAY, false)  // 服务端积累数据后发送，有延迟
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持长连接
            channel = server.bind().sync().channel();
            log.info("Netty Server started on {}:{}", config.getHost(), config.getPort());
        }catch (InterruptedException e) {
            log.error("Netty Server interrupted", e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("=========== Netty Server End ============");
        Optional.ofNullable(channel).ifPresent(Channel::close);
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private static class ChannelInit extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            log.info("=========== New Connection ============");
            channel.pipeline()
                    .addLast("http-codec", new HttpServerCodec())
                    .addLast("http-chunked", new ChunkedWriteHandler())
                    .addLast("aggregator", new HttpObjectAggregator(1024))
                    .addLast(new IdleStateHandler(60, 30, 60 * 30, TimeUnit.SECONDS))
                    .addLast(new WebSocketServerProtocolHandler("/", null, true))
                    .addLast("handler", new WebSocketFrameHandler());
        }
    }
}
