package com.future.demo.mapper;

import com.future.demo.entity.MemoryAssistantMyISAMEntity;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MemoryAssistantMyISAMMapper extends MPJBaseMapper<MemoryAssistantMyISAMEntity> {
    @Select("select id from memory_assistant_myisam")
    List<Long> selectIds();

    @Select("select * from memory_assistant_myisam where extraIndexId>=#{startId} and extraIndexId<=#{endId} and randomStr=#{randomStr}")
//    @Select("select * from memory_assistant_myisam where randomStr=#{randomStr}")
    List<MemoryAssistantMyISAMEntity> testReadBufferSize(@Param("startId") long startId,
                                                         @Param("endId") long endId,
                                                         @Param("randomStr") String randomStr);
}
