package com.future.demo.mapper;

import com.future.demo.entity.OrderModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    /*@Select("SELECT * FROM t_order WHERE userId = #{userId} AND productId = #{productId}")
    OrderModel getByUserIdAndProductId(Long userId, Long productId);*/

    @Insert("INSERT INTO t_order (userId, createTime) VALUES (#{userId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(OrderModel orderModel);

    @Delete("DELETE FROM t_order")
    void deleteAll();

    @Select("SELECT * FROM t_order")
    List<OrderModel> selectAll();

    @Select("select * from t_order where userId=#{userId}")
    List<OrderModel> list(Long userId);
}
