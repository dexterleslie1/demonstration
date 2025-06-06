package com.future.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommonMapper {
    @Update("update t_count set count=count+#{count} where flag=#{flag}")
    void updateIncreaseCount(String flag, long count);
}
