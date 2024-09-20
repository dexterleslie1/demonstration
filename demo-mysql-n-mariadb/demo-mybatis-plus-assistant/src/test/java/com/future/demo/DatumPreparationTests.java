package com.future.demo;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.entity.MemoryAssistantJoinEntity;
import com.future.demo.entity.User;
import com.future.demo.mapper.MemoryAssistantJoinMapper;
import com.future.demo.mapper.MemoryAssistantMapper;
import com.future.demo.mapper.UserMapper;
import com.future.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class DatumPreparationTests {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Resource
    MemoryAssistantMapper memoryAssistantMapper;
    @Resource
    MemoryAssistantJoinMapper memoryAssistantJoinMapper;

    /**
     * 用于准备测试数据
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        log.debug("正在准备用户测试数据");
        int totalCount = 1000000;
        int batchCount = 10000;
        int totalConcurrentThread = totalCount / batchCount;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < totalConcurrentThread; i++) {
            int finalI = i;
            int finalBatchCount = batchCount;
            executorService.submit(() -> {
                for (int j = 0; j < finalBatchCount; j++) {
                    User user = new User();
                    user.setName("dexter" + ((finalI * finalBatchCount) + (j + 1)));
                    userService.save(user);
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        log.debug("正在准备memory_assistant和memory_assistant_join测试数据");
        int randomStrMinLength = 32;
        int randomStrMaxLength = 1024;
        totalCount = 5 * 1000000;
        int concurrentThreads = 50;
        batchCount = totalCount / concurrentThreads;
        executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            int finalBatchCount1 = batchCount;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < finalBatchCount1; j++) {
                        String randomStr = RandomStringUtils.randomAlphanumeric(randomStrMinLength, randomStrMaxLength);
                        MemoryAssistantEntity entity = new MemoryAssistantEntity();
                        entity.setRandomStr(randomStr);
                        memoryAssistantMapper.insert(entity);
                        entity.setExtraIndexId(entity.getId());
                        memoryAssistantMapper.updateById(entity);

                        // 准备memory_assistant_join数据
                        MemoryAssistantJoinEntity joinEntity = new MemoryAssistantJoinEntity();
                        joinEntity.setRandomStr(randomStr);
                        randomStr = RandomStringUtils.randomAlphanumeric(randomStrMinLength, randomStrMaxLength);
                        joinEntity.setRandomStr2(randomStr);
                        memoryAssistantJoinMapper.insert(joinEntity);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
    }
}
