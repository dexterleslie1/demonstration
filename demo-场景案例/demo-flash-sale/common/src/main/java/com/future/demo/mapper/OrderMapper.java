package com.future.demo.mapper;

import com.future.demo.entity.OrderModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO t_order (id, userId, createTime,`status`,deleteStatus, merchantId) VALUES (#{id}, #{userId}, #{createTime},#{status},#{deleteStatus},#{merchantId})")
    int insert(OrderModel orderModel);

    @Insert("<script>" +
            "   insert ignore into t_order(id,userId,createTime,`status`,deleteStatus,merchantId) values " +
            "   <foreach collection=\"orderModelList\" item=\"e\" separator=\",\">" +
            "       (#{e.id},#{e.userId},#{e.createTime},#{e.status},#{e.deleteStatus},#{e.merchantId})" +
            "   </foreach>" +
            "</script>")
    void insertBatch(@Param("orderModelList") List<OrderModel> orderModelList);

    @Delete("DELETE FROM t_order")
    void deleteAll();

    @Select("SELECT * FROM t_order")
    List<OrderModel> selectAll();

    /*@Select("select * from t_order where userId=#{userId}")
    List<OrderModel> list(Long userId);*/
}
