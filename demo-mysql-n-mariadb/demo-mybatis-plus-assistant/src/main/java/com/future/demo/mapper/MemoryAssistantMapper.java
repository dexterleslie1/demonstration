package com.future.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.entity.MemoryAssistantEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MemoryAssistantMapper extends BaseMapper<MemoryAssistantEntity> {
    @Select("select id from memory_assistant")
    List<Long> selectIds();
}
