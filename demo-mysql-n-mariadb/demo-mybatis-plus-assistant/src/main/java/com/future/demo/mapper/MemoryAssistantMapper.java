package com.future.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.entity.MemoryAssistantEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MemoryAssistantMapper extends BaseMapper<MemoryAssistantEntity> {
    @Select("select id from memory_assistant")
    List<Long> selectIds();

    @Select("select * from memory_assistant where extraIndexId>=#{startId} and extraIndexId<=#{endId} order by randomStr desc limit #{startIndex},#{length}")
    List<MemoryAssistantEntity> list(@Param("startId") long startId,
                                     @Param("endId") long endId,
                                     @Param("startIndex") int startIndex,
                                     @Param("length") int length);
}
