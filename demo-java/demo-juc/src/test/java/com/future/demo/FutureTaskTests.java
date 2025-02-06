package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
@Slf4j
public class FutureTaskTests {
    @Test
    public void test() throws ExecutionException, InterruptedException {
        // region Future+Callable+Thread 有返回值

        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                return "Hello world!";
            }
        });
        Thread thread = new Thread(futureTask);
        thread.start();

        // 主线程继续执行其他任务

        String result = futureTask.get();
        Assert.assertEquals("Hello world!", result);

        // endregion

        // region 结合线程池提升性能

        ExecutorService executorService = Executors.newCachedThreadPool();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        FutureTask<Void> futureTask1 = new FutureTask<>(() -> {
            TimeUnit.SECONDS.sleep(1);
            return null;
        });
        List<Future<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Future future = executorService.submit(futureTask1);
            futureList.add(future);
        }

        for (Future<Void> future : futureList) {
            future.get();
        }

        executorService.shutdown();

        stopWatch.stop();
        Assert.assertTrue(String.valueOf(stopWatch.getTotalTimeMillis()), stopWatch.getTotalTimeMillis() <= 1200);

        // endregion
    }
}
