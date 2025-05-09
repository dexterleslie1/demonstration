package com.future.demo.mapper;

import com.future.demo.entity.OrderDetailModel;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    @Select("<script>" +
            "SELECT * FROM t_order_detail where orderId in " +
            "   <foreach item=\"item\" collection=\"orderIdList\" separator=\",\" open=\"(\" close=\")\">" +
            "       #{item}" +
            "   </foreach>" +
            "</script>")
        // long 类型
        /*List<OrderDetailModel> list(List<Long> orderIdList);*/
        // int 类型
    /*List<OrderDetailModel> list(List<Integer> orderIdList);*/
    // biginteger 类型
    List<OrderDetailModel> list(List<BigInteger> orderIdList);
    // uuid string 类型
    /*List<OrderDetailModel> list(List<String> orderIdList);*/

    @Insert("insert ignore into t_order_detail(id,orderId,userId,productId,merchantId,amount) values(#{orderDetail.id},#{orderDetail.orderId},#{orderDetail.userId},#{orderDetail.productId},#{orderDetail.merchantId},#{orderDetail.amount})")
    int insert(@Param(value = "orderDetail") OrderDetailModel orderDetailModel);

    @Insert("<script>" +
            "   insert ignore into t_order_detail(id,orderId,userId,productId,merchantId,amount) values " +
            "   <foreach collection=\"orderDetailModelList\" item=\"e\" separator=\",\">" +
            "       (#{e.id},#{e.orderId},#{e.userId},#{e.productId},#{e.merchantId},#{e.amount})" +
            "   </foreach>" +
            "</script>")
    void insertBatch(List<OrderDetailModel> orderDetailModelList);

    @Select("select * from t_order_detail where userId=#{userId} and productId=#{productId}")
    OrderDetailModel getByUserIdAndProductId(
            @Param(value = "userId") Long userId,
            @Param(value = "productId") Long productId);

    @Delete("delete from t_order_detail")
    void deleteAll();

    @Select("select * from t_order_detail")
    List<OrderDetailModel> selectAll();
}
