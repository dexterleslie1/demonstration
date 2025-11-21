package com.future.demo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于tcp socket服务器
 */
public class TcpSocketServer {
    /**
     *
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);

        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Tcp socket服务器已启动，监听端口 " + port);

        // 等待socket客户端连接
        final Socket socket = serverSocket.accept();
        InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

        // 每5秒向客户端写数据
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        threadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = "来自服务器：" + System.currentTimeMillis();
                    writer.write(message);
                    // 写入换行符，便于客户端按行读取
                    writer.newLine();
                    // 刷新缓冲区
                    writer.flush();
                    System.out.println("已向客户端发送: " + message);
                } catch (IOException e) {
                    System.out.println("向客户端写数据失败，连接可能已断开");
                    e.printStackTrace();
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        try {
            while (true) {
                // 读取客户端数据
                String message = reader.readLine();

                // 表示客户端已关闭连接
                if (message == null) {
                    System.out.println("客户端已关闭连接");
                    break;
                }

                System.out.println("来自客户端ip=" + socketAddress.getAddress() + ",port=" + socketAddress.getPort() + "的消息：" + message);
            }
        } catch (Exception ex) {
            System.out.println("连接异常断开: " + ex.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

        threadPool.shutdown();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);

        // 关闭socket服务器
        serverSocket.close();
    }
}
