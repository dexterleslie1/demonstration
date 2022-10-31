package com.future.demo.performance;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NettyClient {
    public static void main(String[] args) throws Exception {
        if(args == null || args.length < 2) {
            throw new Exception("指定完整的调用参数，例如：java -jar target/demo-netty-client.jar 目标ip 目标端口");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                        ch.pipeline().addLast("bytesDecoder", new ByteArrayDecoder() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //log.debug("服务器消息：{}", msg);
                            }
                        });
                        // Encoder
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ch.pipeline().addLast("bytesEncoder", new ByteArrayEncoder());
                    }
                });

        Channel channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(()-> {
            byte []datum = new byte[1024 * 1024];
            Random random = new Random();
            random.nextBytes(datum);

            boolean b = true;
            while (b) {
                ChannelFuture channelFuture = channel.writeAndFlush(datum);
                try {
                    channelFuture.sync();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }
}
