package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端
 */
@Slf4j
public class BioClient {
    public static void main(String[] args) throws Exception {
        if(args == null || args.length < 3) {
            throw new Exception("指定完整的调用参数，例如：java -jar target/demo-socket-client.jar 目标ip 目标端口 并发socket数");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int count = Integer.parseInt(args[2]);

        List<Socket> socketList = new ArrayList<>();
        long start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            Socket socket = new Socket(host, port);
            socketList.add(socket);
        }
        long end = System.currentTimeMillis();
        System.out.println("建立socket连接" + count + "个耗时" + (end - start) + "毫秒");

        socketList.forEach(socket -> {
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.println("你好世界");
                writer.flush();
                String mess = reader.readLine();
//                System.out.println("服务器消息：" + mess);
                writer.close();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
