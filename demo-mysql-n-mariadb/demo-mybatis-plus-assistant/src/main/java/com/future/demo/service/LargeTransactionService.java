package com.future.demo.service;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.mapper.MemoryAssistantMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟大事务
 */
@Service
public class LargeTransactionService {
    @Autowired
    MemoryAssistantMapper memoryAssistantMapper;

    /**
     * 执行大事务
     * @param statementCount 事务中执行的语句数
     */
    @Transactional(rollbackFor = Throwable.class)
    public void execute(int statementCount) {
        int randomStrMinLength = 32;
        int randomStrMaxLength = 1024;

        // 插入多条数据，稍后会删除
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i <= statementCount; i++) {
            MemoryAssistantEntity entity = new MemoryAssistantEntity();
            String randomStr = RandomStringUtils.randomAlphanumeric(randomStrMinLength, randomStrMaxLength);
            entity.setRandomStr(randomStr);
            memoryAssistantMapper.insert(entity);
            idList.add(entity.getId());
        }

        for (int i = 0; i < idList.size(); i++) {
            String randomStr = RandomStringUtils.randomAlphanumeric(randomStrMinLength, randomStrMaxLength);
            MemoryAssistantEntity entity = new MemoryAssistantEntity();
            entity.setId(idList.get(i));
            entity.setRandomStr(randomStr);
            memoryAssistantMapper.updateById(entity);
        }

        for (int i = 0; i < idList.size(); i++) {
            memoryAssistantMapper.deleteById(idList.get(i));
        }

        String randomStr = RandomStringUtils.randomAlphanumeric(randomStrMinLength, randomStrMaxLength);
        MemoryAssistantEntity entity = new MemoryAssistantEntity();
        entity.setRandomStr(randomStr);
        memoryAssistantMapper.insert(entity);
        entity.setExtraIndexId(entity.getId());
        memoryAssistantMapper.updateById(entity);
    }
}
