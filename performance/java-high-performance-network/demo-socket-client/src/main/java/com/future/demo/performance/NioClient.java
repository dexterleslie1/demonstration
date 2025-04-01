package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NioClient {
    private final static String localCharset = "UTF-8";

    static boolean stop = false;
    public static void main(String[] args) throws Exception {
        if(args == null || args.length < 3) {
            throw new Exception("指定完整的调用参数，例如：java -jar target/demo-socket-client.jar 目标ip 目标端口 并发socket数");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int count = Integer.parseInt(args[2]);

        Selector selector = Selector.open();

        long start = System.currentTimeMillis();
        long prev = System.currentTimeMillis();
        for(int i=1; i<=count; i++) {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
            socketChannel.connect(inetSocketAddress);

            long current = System.currentTimeMillis();
            if(current - prev >= 1000) {
                prev = System.currentTimeMillis();
                log.debug("正在建立第{}个socket连接", i);
            }
        }
        long end = System.currentTimeMillis();
        log.debug("建立共 {} 个socket连接耗时 {} 毫秒", count, end - start);

        List<SocketChannel> socketChannelList = new ArrayList<>();
        Thread thread = new Thread(() -> {
            while(!stop) {
                try {
                    selector.select();

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey selectionKey = keyIterator.next();
                        keyIterator.remove();
                        try {
                            //连接请求
                            if (selectionKey.isConnectable()) {
                                //获取channel
                                SocketChannel socketChannelTemp = (SocketChannel) selectionKey.channel();
                                if (socketChannelTemp.finishConnect()) {
                                    //非阻塞
                                    socketChannelTemp.configureBlocking(false);
                                    //注册selector
                                    socketChannelTemp.register(selectionKey.selector(), SelectionKey.OP_READ, null);

                                    // log.debug("成功建立socket连接 {}", socketChannelTemp);

                                    socketChannelList.add(socketChannelTemp);

                                    // ByteBuffer byteBuffer = ByteBuffer.wrap("你好世界".getBytes());
                                    // socketChannelTemp.write(byteBuffer);
                                }
                            } else if (selectionKey.isReadable()) {
                                //读请求
                                SocketChannel socketChannelTemp = (SocketChannel) selectionKey.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                if (socketChannelTemp.read(buffer) == -1) {
                                    // log.debug("关闭socket连接 {}", socketChannelTemp);

                                    socketChannelList.remove(socketChannelTemp);
                                    socketChannelTemp.close();
                                } else {
                                    //将channel改为读取状态
                                    buffer.flip();
                                    //按照编码读取数据
                                    String receivedStr = Charset.forName(localCharset).newDecoder().decode(buffer).toString();

                                    // log.debug("服务器消息：{}", receivedStr);

                                    buffer.clear();
                                }
                            }
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        });
        thread.start();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<10; i++) {
            executorService.submit(()->{
                Random random = new Random();
                while (!stop) {
                    int length = socketChannelList.size();
                    if (length <= 0) {
                        log.debug("不要删除，占位符号，否则程序不能正常工作");
                        continue;
                    }

                    int index = random.nextInt(length);
                    try {
                        socketChannelList.get(index).write(Charset.defaultCharset().encode("888"));
                    } catch (IOException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            });
        }

        System.out.println("输入任意键退出程序");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        log.debug("准备退出程序");
        stop = true;
        selector.wakeup();
    }
}
