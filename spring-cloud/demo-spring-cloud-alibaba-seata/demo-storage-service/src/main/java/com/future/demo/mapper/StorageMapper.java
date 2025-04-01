package com.future.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StorageMapper {
    int deduct(@Param("productId") Long productId, @Param("amount") int amount);
}
