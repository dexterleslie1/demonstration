package com.future.demo.performance;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端
 */
@Slf4j
public class NettyServer {
    private final static AtomicInteger ConnectedSocketCount = new AtomicInteger();
//    private final static String localCharset = "UTF-8";

    public static void main(String[] args) {
        int port = 9090;

        new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .option(ChannelOption.SO_RCVBUF, 128*1024)
//                .option(ChannelOption.SO_SNDBUF, 128*1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
//                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .childOption(ChannelOption.SO_RCVBUF, 128*1024)
//                .childOption(ChannelOption.SO_SNDBUF, 128*1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new StringEncoder());
//                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//
//                            @Override
//                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                ConnectedSocketCount.incrementAndGet();
//
//                                super.channelActive(ctx);
//                            }
//
//                            @Override
//                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//                                ConnectedSocketCount.decrementAndGet();
//
//                                super.channelInactive(ctx);
//                            }
//
//                            @Override
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                 log.debug("客户端消息：{}", msg);
//                                ctx.channel().writeAndFlush(msg);
//                            }
//                        });
                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                        ch.pipeline().addLast("bytesDecoder", new ByteArrayDecoder() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                log.debug("客户端消息：{}", msg);
                                ctx.channel().writeAndFlush(msg);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ConnectedSocketCount.incrementAndGet();

                                super.channelActive(ctx);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                ConnectedSocketCount.decrementAndGet();

                                super.channelInactive(ctx);
                            }
                        });
                        // Encoder
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ch.pipeline().addLast("bytesEncoder", new ByteArrayEncoder());
                    }
                }).bind(port);
        log.debug("服务器已经启动，端口： {}", port);

        doCronbDisplayInfo();
    }

    static void doCronbDisplayInfo() {
        Thread thread = new Thread(() -> {
            while (true) {
                log.debug("当前已连接socket： {}", ConnectedSocketCount.get());

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        thread.start();
    }
}
