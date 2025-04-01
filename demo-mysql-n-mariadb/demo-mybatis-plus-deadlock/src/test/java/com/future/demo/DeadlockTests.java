package com.future.demo;

import com.future.demo.service.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class DeadlockTests {

    @Resource
    private CourseService courseService;

    @Test
    public void test() throws InterruptedException {
        Long id = 5L;
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                courseService.tx1(id);
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                courseService.tx2(id);
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
    }

}