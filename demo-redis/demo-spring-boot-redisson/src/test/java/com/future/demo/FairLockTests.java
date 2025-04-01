package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FairLockTests {
    @Resource
    RedissonClient redisson = null;

    @Test
    public void test() throws InterruptedException {
        String key = UUID.randomUUID().toString();

        // 按照加锁的请求顺序公平地（先到先得）分配上锁机会
        RLock rLock = this.redisson.getFairLock(key);
        List<String> seqList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                seqList.add(Thread.currentThread().getName());
                boolean acquired = false;
                try {
                    acquired = rLock.tryLock(100000, 10000, TimeUnit.MILLISECONDS);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (acquired) {
                        try {
                            rLock.unlock();
                        } catch (Exception ignored) {
                            //
                        }
                    }
                }
            });
            thread.setName("thread-" + (i + 1));
            thread.start();

            TimeUnit.MILLISECONDS.sleep(10);
        }

        TimeUnit.SECONDS.sleep(2);

        Assert.assertEquals(5, seqList.size());
        Assert.assertEquals(seqList.get(0), "thread-1");
        Assert.assertEquals(seqList.get(1), "thread-2");
        Assert.assertEquals(seqList.get(2), "thread-3");
        Assert.assertEquals(seqList.get(3), "thread-4");
        Assert.assertEquals(seqList.get(4), "thread-5");
    }
}
