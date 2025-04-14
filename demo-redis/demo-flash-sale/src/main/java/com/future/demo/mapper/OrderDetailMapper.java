package com.future.demo.mapper;

import com.future.demo.entity.OrderDetailModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    @Select("<script>" +
            "SELECT * FROM t_order_detail where orderId in " +
            "   <foreach item=\"item\" collection=\"orderIdList\" separator=\",\" open=\"(\" close=\")\">" +
            "       #{item}" +
            "   </foreach>" +
            "</script>")
    List<OrderDetailModel> list(List<Long> orderIdList);

    @Insert("insert into t_order_detail(orderId,userId,productId,amount) values(#{orderDetail.orderId},#{orderDetail.userId},#{orderDetail.productId},#{orderDetail.amount})")
    void insert(@Param(value = "orderDetail") OrderDetailModel orderDetailModel);

    @Select("select * from t_order_detail where userId=#{userId} and productId=#{productId}")
    OrderDetailModel getByUserIdAndProductId(
            @Param(value = "userId") Long userId,
            @Param(value = "productId") Long productId);

    @Delete("delete from t_order_detail")
    void deleteAll();
}
