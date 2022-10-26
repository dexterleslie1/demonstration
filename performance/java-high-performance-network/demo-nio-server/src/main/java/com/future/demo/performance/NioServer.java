package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端
 * https://www.jb51.net/article/214925.htm
 */
@Slf4j
public class NioServer {
    private final static AtomicInteger ConnectedSocketCount = new AtomicInteger();
    private final static String localCharset = "UTF-8";

    public static void main(String[] args) {
        int port = 9090;
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 10000000);
            //设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //为serverChannel注册selector
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.debug("服务器已经启动，端口： {}", port);

            doCronbDisplayInfo();

            while (true) {
                selector.select();
                //获取selectionKeys并处理
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    keyIterator.remove();

                    try {
                        //连接请求
                        if (selectionKey.isAcceptable()) {
                            //获取channel
                            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                            //非阻塞
                            socketChannel.configureBlocking(false);
                            //注册selector
                            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ, null);

                            // log.debug("客户端连接 {}", socketChannel);

                            ConnectedSocketCount.incrementAndGet();
                        }else if (selectionKey.isReadable()) {
                            try {
                                //读请求
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                if (socketChannel.read(buffer) == -1) {
                                    ConnectedSocketCount.decrementAndGet();

                                    // log.debug("关闭socket连接 {}", socketChannel);

                                    // 客户端关闭连接
                                    socketChannel.close();
                                } else {
                                    //将channel改为读取状态
                                    buffer.flip();
                                    //按照编码读取数据
                                    String receivedStr = Charset.forName(localCharset).newDecoder().decode(buffer).toString();

                                    // log.debug("客户端消息：{}", receivedStr);

                                    buffer.clear();
                                    //返回数据给客户端
                                    buffer = buffer.put((receivedStr).getBytes(localCharset));
                                    //读取模式
                                    buffer.flip();
                                    socketChannel.write(buffer);
                                    buffer.clear();
                                }
                            } catch (Exception ex) {
                                ConnectedSocketCount.decrementAndGet();
                                selectionKey.cancel();
                            }
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
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
