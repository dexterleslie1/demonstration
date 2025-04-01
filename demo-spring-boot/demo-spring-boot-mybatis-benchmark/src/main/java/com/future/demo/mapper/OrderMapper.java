package com.future.demo.mapper;

import com.future.demo.bean.DeleteStatus;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("insert into `order`(create_time,user_id,merchant_id,total_amount,total_count,status,pay_time,delivery_time," +
            "received_time,cancel_time,delete_status) " +
            "values(#{createTime},#{userId},#{merchantId},#{totalAmount},#{totalCount}," +
            "#{status},#{payTime},#{deliveryTime},#{receivedTime},#{cancelTime},#{deleteStatus})")
    void add(Order order);

    @Insert("<script>" +
            "insert into `order`(create_time,user_id,merchant_id,total_amount,total_count,status,pay_time,delivery_time," +
            "received_time,cancel_time,delete_status) values " +
            "   <foreach item=\"e\" collection=\"orderList\" separator=\",\">" +
            "       (#{e.createTime},#{e.userId},#{e.merchantId},#{e.totalAmount},#{e.totalCount},#{e.status},#{e.payTime}," +
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

    @Select("select distinct user_id from `order`")
    List<Long> listUserIdAll();

    @Select("select distinct merchant_id from `order`")
    List<Long> listMerchantIdAll();

    @Select("select id from `order`")
    List<Long> listIdAll();

    List<Order> listByUserId(@Param("userId") Long userId,
                             @Param("status") Status status,
                             @Param("deleteStatus") DeleteStatus deleteStatus,
                             @Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime,
                             @Param("start") Long start,
                             @Param("size") Long size);

    List<Order> listByMerchantId(@Param("merchantId") Long merchantId,
                                 @Param("status") Status status,
                                 @Param("deleteStatus") DeleteStatus deleteStatus,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime,
                                 @Param("start") Long start,
                                 @Param("size") Long size);
}
