package com.coldwindx.lokinetty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        log.info("\nchannel id: {}", ctx.channel().id().asLongText());
        if(frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            log.info("RECV: {}", request);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("world"));
            return;
        }
        if(frame instanceof BinaryWebSocketFrame){
            ByteBuf content = frame.content();
            byte[] bytes = new byte[content.readableBytes()];
            content.readBytes(bytes);
            String request = new String(bytes);
            log.info("RECV: {}", request);
            ByteBuf response = Unpooled.copiedBuffer("world".getBytes());
            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(response));
            return;
        }
        throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("\nSTART TO CONNECTION, channel id: {}", ctx.channel().id().asLongText());
//        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("\n SUCCESS TO CONNECTION, channel id: {}", ctx.channel().id().asLongText());
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("HEAD...");
        super.userEventTriggered(ctx, evt);
    }
}
