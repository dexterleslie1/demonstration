package com.future.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("HTTP服务器已启动，监听端口 " + port);

        // 等待socket客户端连接
        final Socket socket = serverSocket.accept();

        // 读取客户端请求数据
        InputStream rawInputStream = socket.getInputStream();
        ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
        while (true) {
            int byteDatum = rawInputStream.read();

            // 客户端断开连接
            if (byteDatum == -1) {
                break;
            }

            if (byteDatum == '\r') {
                byteDatum = rawInputStream.read();
                if (byteDatum == '\n') {
                    // 读取到空行表示请求数据读取结束
                    if (lineBuffer.size() == 0) {
                        break;
                    }

                    // 读取到一行数据
                    String line = new String(lineBuffer.toByteArray(), StandardCharsets.UTF_8);
                    lineBuffer = new ByteArrayOutputStream();
                    System.out.println("< " + line);
                }
            } else {
                lineBuffer.write(byteDatum);
            }
        }

        // 响应客户端请求
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        writer.write("\r\n");
        writer.write("Hello world!");
        writer.flush();

        socket.close();
        serverSocket.close();
    }
}
