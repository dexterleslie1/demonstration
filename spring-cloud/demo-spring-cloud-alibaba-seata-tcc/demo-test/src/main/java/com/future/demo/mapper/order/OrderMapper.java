package com.future.demo.mapper.order;

import com.future.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("select * from t_order")
    List<Order> selectAll();
}
