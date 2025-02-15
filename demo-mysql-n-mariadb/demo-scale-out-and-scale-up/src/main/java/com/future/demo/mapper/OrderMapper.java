package com.future.demo.mapper;

import com.future.demo.bean.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("insert into `order`(id,create_time,user_id,merchant_id,total_amount,total_count,status,pay_time,delivery_time," +
            "received_time,cancel_time,delete_status) " +
            "values(#{id},#{createTime},#{userId},#{merchantId},#{totalAmount},#{totalCount}," +
            "#{status},#{payTime},#{deliveryTime},#{receivedTime},#{cancelTime},#{deleteStatus})")
    void add(Order order);

    @Insert("<script>" +
            "insert into `order`(id,create_time,user_id,merchant_id,total_amount,total_count,status,pay_time,delivery_time," +
            "received_time,cancel_time,delete_status) values " +
            "   <foreach item=\"e\" collection=\"orderList\" separator=\",\">" +
            "       (#{e.id},#{e.createTime},#{e.userId},#{e.merchantId},#{e.totalAmount},#{e.totalCount},#{e.status},#{e.payTime}," +
            "       #{e.deliveryTime},#{e.receivedTime},#{e.cancelTime},#{e.deleteStatus})" +
            "   </foreach>" +
            "</script>")
    void addBatch(@Param("orderList") List<Order> orderList);

    @Select("select * from `order` where id=#{id}")
    Order get(@Param("id") Long id);

    @Select("select count(id) from `order`")
    int count();

    @Update("truncate table `order`")
    void truncate();

    @Select("select * from `order` where user_id=#{userId} order by id desc")
    List<Order> findByUserId(@Param("userId") Long userId);
}
