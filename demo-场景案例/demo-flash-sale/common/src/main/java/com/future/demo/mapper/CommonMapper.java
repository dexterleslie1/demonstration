package com.future.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CommonMapper {
    @Insert("insert ignore into t_count_idempotent(idempotentId) values(#{idempotentId})")
    int insertCountIdempotent(String idempotentId);

    @Update("update t_count set count=count+#{count} where flag=#{flag}")
    void updateIncreaseCount(String flag, long count);

    @Select("select `count` from t_count where flag=#{flag}")
    Long getCountByFlag(String flag);
}
