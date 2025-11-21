package com.future.demo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于tcp socket客户端
 */
public class TcpSocketClient {
    /**
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);

        String host = "localhost";
        int port = 8080;

        SocketAddress socketAddress = new InetSocketAddress(host, port);
        Socket socket = new Socket();
        socket.connect(socketAddress);

        // 每5秒向服务器写数据
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        threadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = "来自客户端：" + System.currentTimeMillis();
                    writer.write(message);
                    // 写入换行符，便于客户端按行读取
                    writer.newLine();
                    // 刷新缓冲区
                    writer.flush();
                    System.out.println("已向服务器发送: " + message);
                } catch (IOException e) {
                    System.out.println("向服务器写数据失败，连接可能已断开");
                    e.printStackTrace();
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        while (true) {
            // 读取服务器数据
            String message = reader.readLine();

            // 表示服务器已关闭连接
            if (message == null) {
                System.out.println("服务器已关闭连接");
                break;
            }

            System.out.println("来自服务器的消息：" + message);
        }

        threadPool.shutdown();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);

        socket.close();
    }
}
