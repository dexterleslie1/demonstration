package com.future.demo.mapper;

import com.future.demo.entity.OrderModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM t_order WHERE userId = #{userId} AND productId = #{productId}")
    OrderModel getByUserIdAndProductId(Long userId, Long productId);

    @Insert("INSERT INTO t_order (userId, productId, amount, createTime) VALUES (#{userId}, #{productId}, #{amount}, #{createTime})")
    int insert(OrderModel orderModel);

    @Delete("DELETE FROM t_order")
    void deleteAll();

    @Select("SELECT * FROM t_order")
    List<OrderModel> selectAll();
}
