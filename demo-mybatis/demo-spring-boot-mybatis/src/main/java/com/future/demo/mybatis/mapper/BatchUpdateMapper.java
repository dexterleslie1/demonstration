package com.future.demo.mybatis.mapper;

import com.future.demo.mybatis.entity.BatchUpdateModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// mybatis批量更新的几种方式
// https://blog.csdn.net/sunny_fengjing/article/details/119412563
@Mapper
@Repository
public interface BatchUpdateMapper {
    void truncate();

    int count();

    void insert(@Param("modelList") List<BatchUpdateModel> modelList);

    List<Long> findAllId();

    void update1(@Param("model") BatchUpdateModel model);

    void update2(@Param("modelList") List<BatchUpdateModel> modelList);
}
