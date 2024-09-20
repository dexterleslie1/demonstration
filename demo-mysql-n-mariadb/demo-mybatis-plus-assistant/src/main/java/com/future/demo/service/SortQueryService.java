package com.future.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.mapper.MemoryAssistantMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SortQueryService {
    @Resource
    MemoryAssistantMapper memoryAssistantMapper;

    public List<MemoryAssistantEntity> test(long startId, long endId, int startIndex, int length) {
        LambdaQueryWrapper<MemoryAssistantEntity> queryWrapper
                = new LambdaQueryWrapper<>();
        queryWrapper
                .ge(MemoryAssistantEntity::getExtraIndexId, startId)
                .le(MemoryAssistantEntity::getExtraIndexId, endId)
                .orderByDesc(MemoryAssistantEntity::getRandomStr)
                .last("limit " + startIndex + "," + length);

        return this.memoryAssistantMapper.selectList(queryWrapper);
    }
}
