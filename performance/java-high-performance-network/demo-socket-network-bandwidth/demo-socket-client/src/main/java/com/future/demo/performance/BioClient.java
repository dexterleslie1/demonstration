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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端
 */
@Slf4j
public class BioClient {
    public static void main(String[] args) throws Exception {
        if(args == null || args.length < 2) {
            throw new Exception("指定完整的调用参数，例如：java -jar target/demo-socket-client.jar 目标ip 目标端口");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket socket = new Socket(host, port);

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(()-> {
            try {
                byte []datum = new byte[1024 * 1024];
                Random random = new Random();
                random.nextBytes(datum);

                OutputStream outputStream = socket.getOutputStream();
                boolean b = true;
                while (b) {
                    outputStream.write(datum);
                    outputStream.flush();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

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


        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        socket.close();
    }
}
