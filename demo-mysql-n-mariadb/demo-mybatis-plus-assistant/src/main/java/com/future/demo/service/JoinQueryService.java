package com.future.demo.service;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.entity.MemoryAssistantJoinEntity;
import com.future.demo.mapper.MemoryAssistantJoinMapper;
import com.future.demo.mapper.MemoryAssistantMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class JoinQueryService {
    @Resource
    MemoryAssistantMapper memoryAssistantMapper;
    @Resource
    MemoryAssistantJoinMapper memoryAssistantJoinMapper;

    public List<Map<String, Object>> test(long startId, long endId, int startIndex, int length) {
        MPJLambdaWrapper<MemoryAssistantEntity> queryWrapper
                = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(MemoryAssistantEntity.class)
                .select(MemoryAssistantJoinEntity::getRandomStr2)
                .leftJoin(MemoryAssistantJoinEntity.class, MemoryAssistantJoinEntity::getRandomStr, MemoryAssistantEntity::getRandomStr)
                .ge(MemoryAssistantEntity::getExtraIndexId, startId)
                .le(MemoryAssistantEntity::getExtraIndexId, endId)
                .last("limit " + startIndex + "," + length);

        return this.memoryAssistantMapper.selectJoinMaps(queryWrapper);
    }
}
