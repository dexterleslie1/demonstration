package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class Tests {

    /**
     * https://blog.csdn.net/MadLifeBin/article/details/120550966
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        List<StopWatch> stopWatchList = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 2; i++) {
            int finalI = i;
            executorService.submit(() -> {
                StopWatch stopWatch = new StopWatch("任务-" + finalI);
                stopWatchList.add(stopWatch);

                // 执行任务步骤2
                String taskName = "任务-" + finalI + "-step1";
                stopWatch.start(taskName);

                Random random = new Random();
                int milliseconds = random.nextInt(1000);
                if (milliseconds <= 0) {
                    milliseconds = 1;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                } catch (InterruptedException ignored) {

                }

                stopWatch.stop();

                // 执行任务步骤2
                taskName = "任务-" + finalI + "-step2";
                stopWatch.start(taskName);

                milliseconds = random.nextInt(1000);
                if (milliseconds <= 0) {
                    milliseconds = 1;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                } catch (InterruptedException ignored) {

                }

                stopWatch.stop();
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        stopWatchList.forEach(stopWatch -> {
            // 获取任务两个步骤总执行时间
            long totalTimeMilllis = stopWatch.getTotalTimeMillis();
            log.debug(stopWatch.getId() + "执行时间: " + totalTimeMilllis + " 毫秒");

            String stopWatchStr = stopWatch.prettyPrint();
            log.debug(stopWatchStr);
        });
    }

}
