package com.future.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class ArthasService {
    private final static Random RANDOM = new Random();

    private long watchCount = 0;

    /**
     * @param uuid
     * @return
     */
    public Object[] watchMethod(String uuid, int intP) throws Exception {
        watchCount++;

        if (intP == 2) {
            throw new Exception("测试watch命令异常情况");
        }

        String randomUUID = UUID.randomUUID().toString();
        return new Object[]{uuid, intP, randomUUID, new Date()};
    }

    /**
     * 用于辅助测试trace命令
     *
     * @throws InterruptedException
     */
    public void traceMethodLv1() throws InterruptedException {
        sleepRandomly();
        traceMethodLv2();
    }

    private void traceMethodLv2() throws InterruptedException {
        sleepRandomly();
        traceMethodLv3();
    }

    private void traceMethodLv3() throws InterruptedException {
        sleepRandomly();
    }

    private void sleepRandomly() throws InterruptedException {
        int randomInt = RANDOM.nextInt(2000);
        if (randomInt > 0) {
            Thread.sleep(randomInt);
        }
    }
}
