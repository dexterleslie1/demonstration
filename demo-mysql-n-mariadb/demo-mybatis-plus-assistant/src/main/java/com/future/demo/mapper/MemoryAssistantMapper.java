package com.future.demo.mapper;

import com.future.demo.entity.MemoryAssistantEntity;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MemoryAssistantMapper extends MPJBaseMapper<MemoryAssistantEntity> {
    @Select("select id from memory_assistant")
    List<Long> selectIds();

    @Select("select * from memory_assistant where extraIndexId>=#{startId} and extraIndexId<=#{endId} order by randomStr desc limit #{startIndex},#{length}")
    List<MemoryAssistantEntity> list(@Param("startId") long startId,
                                     @Param("endId") long endId,
                                     @Param("startIndex") int startIndex,
                                     @Param("length") int length);

    /**
     * 用于协助测试tmp_table_size参数对内存使用率影响
     * <p>
     * 参考以下资料通过union查询触发内存temp table
     * https://dev.mysql.com/doc/refman/8.4/en/internal-temporary-tables.html
     *
     * @param startId
     * @param endId
     * @return
     */
    @Select("SELECT id,randomStr,extraIndexId FROM memory_assistant " +
            "WHERE extraIndexId >= #{startId} AND extraIndexId <= #{endId} " +
            "union " +
            "SELECT id,randomStr,extraIndexId FROM memory_assistant " +
            "WHERE extraIndexId >= #{startId} AND extraIndexId <= #{endId}")
    List<MemoryAssistantEntity> testUnion(@Param("startId") long startId,
                                          @Param("endId") long endId);
}
