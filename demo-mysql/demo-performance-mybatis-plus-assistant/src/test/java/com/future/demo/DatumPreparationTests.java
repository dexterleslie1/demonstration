package com.future.demo;

import com.future.demo.entity.User;
import com.future.demo.mapper.UserMapper;
import com.future.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class DatumPreparationTests {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    /**
     * 用于准备测试数据
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        int totalCount = 1000000;
        int batchCount = 10000;
        int totalConcurrentThread = totalCount / batchCount;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < totalConcurrentThread; i++) {
            int finalI = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < batchCount; j++) {
                        User user = new User();
                        user.setName("dexter" + ((finalI * batchCount) + (j + 1)));
                        userService.save(user);
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
    }
}
