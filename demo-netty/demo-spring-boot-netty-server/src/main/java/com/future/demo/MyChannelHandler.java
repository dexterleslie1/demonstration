package com.future.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyChannelHandler extends SimpleChannelInboundHandler<String> {

    private final BusinessService businessService;

    public MyChannelHandler(BusinessService businessService) {
        this.businessService = businessService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("收到客户端消息: " + msg);

        // 调用Spring管理的Bean执行业务逻辑
        String result = businessService.processMessage(msg);

        // 向客户端返回响应
        ctx.writeAndFlush("服务器响应: " + result);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端连接: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端断开: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
