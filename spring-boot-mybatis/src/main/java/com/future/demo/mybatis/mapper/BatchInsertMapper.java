package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.BatchInsertModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// 批量插入大量数据最优方式
// https://blog.csdn.net/blueheartstone/article/details/126602810
@Mapper
@Repository
public interface BatchInsertMapper {
    void truncate();

    int count();

    void insert1(@Param("model") BatchInsertModel model);

    void insert2(@Param("modelList") List<BatchInsertModel> modelList);
}
