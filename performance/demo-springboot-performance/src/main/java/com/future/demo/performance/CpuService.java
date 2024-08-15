package com.future.demo.performance;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 用于制造cpu负载
 */
@Service
public class CpuService {
    @Autowired
    PasswordEncoder passwordEncoder;

    private boolean stop = false;
    private ExecutorService executorService;

    /**
     * 开始cpu负载
     */
    public synchronized void startConsume() {
        if (this.executorService == null) {
            ThreadFactory namedThreadFactory =
                    new ThreadFactoryBuilder().setNameFormat("cpu负载-%d").build();
            this.stop = false;
            this.executorService = Executors.newCachedThreadPool(namedThreadFactory);
            for (int i = 0; i < 2; i++) {
                this.executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        while (!stop) {
                            consume();
                        }
                    }
                });
            }
        }
    }

    /**
     * 停止cpu负载
     *
     * @throws InterruptedException
     */
    public synchronized void stopConsume() throws InterruptedException {
        this.stop = true;

        if (this.executorService != null) {
            this.executorService.shutdown();
            while (!this.executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
            this.executorService = null;
        }
    }

    public String consume() {
        String uuid = UUID.randomUUID().toString();
        String encoded = this.passwordEncoder.encode(uuid);
        return "密码 " + uuid + " encode结果 " + encoded;
    }

}
