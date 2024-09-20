package com.future.demo;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.entity.MemoryAssistantJoinEntity;
import com.future.demo.mapper.MemoryAssistantJoinMapper;
import com.future.demo.mapper.MemoryAssistantMapper;
import org.apache.commons.lang3.RandomStringUtils;
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
public class MemoryAssistantPreparationTests {
    @Resource
    MemoryAssistantMapper memoryAssistantMapper;
    @Resource
    MemoryAssistantJoinMapper memoryAssistantJoinMapper;

    /**
     * 准备协助测试mysql内存数据
     */
    @Test
    public void testPrepareDatum() throws InterruptedException {
        int randomStrMinLength = 32;
        int randomStrMaxLength = 1024;
        int totalCount = 5 * 1000000;
        int concurrentThreads = 50;
        int batchCount = totalCount / concurrentThreads;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < batchCount; j++) {
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
