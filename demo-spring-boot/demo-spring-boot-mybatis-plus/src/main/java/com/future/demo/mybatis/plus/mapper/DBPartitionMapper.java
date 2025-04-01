package com.future.demo.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.mybatis.plus.entity.DBPartition;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DBPartitionMapper extends BaseMapper<DBPartition> {
    @Update("alter table dan add partition (partition ${partitionName} values less than (to_days(#{date})))")
    void add(@Param("partitionName") String partitionName,
             @Param("date") Date date);

    @Delete("alter table dan drop partition ${partitionName}")
    void delete(@Param("partitionName") String partitionName);

    @Select("select par.partition_name from information_schema.partitions par " +
            "where par.table_schema='mybatisplusdemo' and par.table_name='dan' " +
            "and par.partition_name!='pdefault'")
    List<Map<String, Object>> findPartitions();
}
