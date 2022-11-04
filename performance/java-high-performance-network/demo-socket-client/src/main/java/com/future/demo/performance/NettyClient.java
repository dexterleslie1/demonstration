package com.future.demo.performance;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {
    public static void main(String[] args) throws Exception {
        if(args == null || args.length < 3) {
            throw new Exception("指定完整的调用参数，例如：java -jar target/demo-socket-client.jar 目标ip 目标端口 并发socket数");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int count = Integer.parseInt(args[2]);

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(10);
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .option(ChannelOption.SO_RCVBUF, 128*1024)
//                .option(ChannelOption.SO_SNDBUF, 128*1024)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new StringEncoder());
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                            @Override
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                log.debug("服务器消息：{}", msg);
////                                ctx.channel().writeAndFlush(msg);
//                            }
//                        });

                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                        ch.pipeline().addLast("bytesDecoder", new ByteArrayDecoder() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                log.debug("服务器消息：{}", msg);
                                ctx.channel().writeAndFlush(msg);
                            }
                        });
                        // Encoder
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ch.pipeline().addLast("bytesEncoder", new ByteArrayEncoder());
                    }
                });

        long start = System.currentTimeMillis();
        long prev = System.currentTimeMillis();
        Random random = new Random();
        for(int i=1; i<=count; i++) {
            byte [] datum = new byte[64*1024];
            random.nextBytes(datum);
//            String str = Base64.getEncoder().encodeToString(datum);
//            bootstrap.connect(new InetSocketAddress(host, port)).sync().channel().writeAndFlush(str);
            bootstrap.connect(new InetSocketAddress(host, port)).sync().channel().writeAndFlush(datum);
            long current = System.currentTimeMillis();
            if(current - prev >= 1000) {
                prev = System.currentTimeMillis();
                log.debug("正在建立第{}个socket连接", i);
            }
        }
        long end = System.currentTimeMillis();
        log.debug("建立共 {} 个socket连接耗时 {} 毫秒", count, end - start);
    }
}
