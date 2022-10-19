package com.future.demo.performance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 服务端
 */
public class BioServer {
    public static void main(String[] args) throws InterruptedException {
        int port = 9090;
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器已经启动，端口：" + port);

            while(true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String mess = br.readLine();
//                        System.out.println("客户端消息：" + mess);
                        PrintWriter writer = new PrintWriter(socket.getOutputStream());
                        writer.println(mess);
                        writer.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            executorService.shutdown();
            while(!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        }
    }
}
