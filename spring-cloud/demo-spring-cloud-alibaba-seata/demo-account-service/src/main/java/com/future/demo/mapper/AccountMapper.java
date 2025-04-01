package com.future.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface AccountMapper {
    int deduct(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
