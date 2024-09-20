package com.future.demo.service;

import com.future.demo.mapper.MemoryAssistantMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TempTableService {
    @Resource
    MemoryAssistantMapper memoryAssistantMapper;

    public List<Map<String, Object>> test(long startId, long endId) {
        return this.memoryAssistantMapper.testGroupBy(startId, endId);
    }
}
