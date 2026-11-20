package com.future.demo.provider.mapper;

import com.future.demo.common.entity.BpkcMxb;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;

public interface BpkcMxbMapper {
    List<Long> selectAllIds();

    /**
     * 从 startId 起连续取 limit 条，Cursor 流式读取。
     */
    Cursor<BpkcMxb> streamFromId(@Param("startId") long startId, @Param("limit") int limit);
}
