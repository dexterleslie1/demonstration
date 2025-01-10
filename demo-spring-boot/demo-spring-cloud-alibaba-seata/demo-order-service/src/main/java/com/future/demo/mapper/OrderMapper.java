package com.future.demo.mapper;

import com.future.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    int insert(Order order);

    Order findById(Long id);

    int update(Order order);
}
