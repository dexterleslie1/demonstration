package com.future.demo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.entity.User;
import com.future.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    private final static Random RANDOM = new Random();

    @Autowired
    UserService userService;

    /**
     * 多个线程读取数据，模拟cpu高情况
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        int maximumId = 1000000;
        int concurrentThreads = 10;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        int randomInt = RANDOM.nextInt(maximumId) + 1;
                        String name = "dexter" + randomInt;
                        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.<User>lambdaQuery();
                        lambdaQueryWrapper.eq(User::getName, name);
                        User user = userService.getOne(lambdaQueryWrapper);
                        user.getId();
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
    }
}
