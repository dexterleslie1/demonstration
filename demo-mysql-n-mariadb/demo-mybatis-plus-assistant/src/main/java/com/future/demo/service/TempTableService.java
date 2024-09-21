package com.future.demo.service;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.mapper.MemoryAssistantMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TempTableService {
    @Resource
    MemoryAssistantMapper memoryAssistantMapper;

    public List<MemoryAssistantEntity> test(long startId, long endId) {
        return this.memoryAssistantMapper.testUnion(startId, endId);
    }
}
