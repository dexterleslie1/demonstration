package com.future.demo.mapper.storage;

import com.future.demo.entity.Order;
import com.future.demo.entity.Storage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StorageMapper {
    @Select("select * from t_storage where product_id=#{productId}")
    Storage getByProductId(@Param("productId") long productId);
}
