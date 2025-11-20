package com.future.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class NettyServerLifecycle {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @Autowired
    private BusinessService businessService;

    // SpringBoot启动时调用
    @PostConstruct
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 连接队列大小
                .option(ChannelOption.SO_BACKLOG, 128)
                // 保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 当服务器接受一个新的客户端连接时，Netty 会创建一个新的 SocketChannel，然后通过 ChannelInitializer来初始化这个通道的 ChannelPipeline。
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加编解码器和业务处理器
                        // Inbound时ByteBuf转换为String
                        pipeline.addLast(new StringDecoder());
                        // Outbound时String转换为ByteBuf
                        pipeline.addLast(new StringEncoder());
                        // Inbound自定义业务处理器
                        pipeline.addLast(new MyChannelHandler(businessService));
                    }
                });

        // 绑定端口并启动服务器
        int port = 8088;
        ChannelFuture future = bootstrap.bind(port).sync();
        channel = future.channel();
        log.info(">>> Netty服务器启动成功，端口：" + port);
    }

    // SpringBoot停止时调用
    @PreDestroy
    public void stop() {
        if (channel != null) {
            channel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        log.info(">>> Netty服务器已关闭");
    }
}
