package com.future.demo.performance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 服务端
 */
public class BioServer {
    public static void main(String[] args) throws IOException {
        int port = 9090;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器已经启动，端口：" + port);

            Socket socket = serverSocket.accept();

            ExecutorService executorService = Executors.newCachedThreadPool();

            executorService.submit(()-> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    byte[] datumT = new byte[1024 * 1024];
                    while (inputStream.read(datumT) != -1) {
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            executorService.submit(()->{
                try {
                    byte []datum = new byte[1024 * 1024];
                    Random random = new Random();
                    random.nextBytes(datum);

                    OutputStream outputStream = socket.getOutputStream();
                    boolean b = true;
                    while(b) {
                        outputStream.write(datum);
                        outputStream.flush();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            executorService.shutdown();
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS));

            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}
