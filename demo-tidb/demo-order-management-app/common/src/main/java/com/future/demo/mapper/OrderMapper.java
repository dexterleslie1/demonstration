package com.future.demo.mapper;

import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO t_order (id, userId, createTime,`status`,deleteStatus) VALUES (#{id}, #{userId}, #{createTime},#{status},#{deleteStatus})")
    int insert(OrderModel orderModel);

    @Insert("<script>" +
            "   insert into t_order(id,userId,createTime,`status`,deleteStatus) values " +
            "   <foreach collection=\"orderModelList\" item=\"e\" separator=\",\">" +
            "       (#{e.id},#{e.userId},#{e.createTime},#{e.status},#{e.deleteStatus})" +
            "   </foreach>" +
            "</script>")
    void insertBatch(@Param("orderModelList") List<OrderModel> orderModelList);

    @Delete("DELETE FROM t_order")
    void deleteAll();

    @Select("SELECT * FROM t_order")
    List<OrderModel> selectAll();

    List<OrderModel> listByUserId(@Param("userId") Long userId,
                                  @Param("status") Status status,
                                  @Param("deleteStatus") DeleteStatus deleteStatus,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("start") Long start,
                                  @Param("size") Long size);

    List<OrderModel> listByMerchantId(@Param("merchantId") Long merchantId,
                                      @Param("status") Status status,
                                      @Param("deleteStatus") DeleteStatus deleteStatus,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("start") Long start,
                                      @Param("size") Long size);

    @Select("select id,userId,`status`,payTime,deliveryTime,receivedTime,cancelTime,deleteStatus,createTime from t_order where id=#{orderId}")
    OrderModel getById(@Param("orderId") Long orderId);

    @Select("<script>" +
            "select id,userId,`status`,payTime,deliveryTime,receivedTime,cancelTime,deleteStatus,createTime from t_order where id in(" +
            "   <foreach collection=\"orderIdList\" item=\"orderId\" separator=\",\">" +
            "       #{orderId}" +
            "   </foreach>" +
            ")" +
            "</script>")
    List<OrderModel> listById(@Param("orderIdList") List<Long> orderIdList);

    @Select("select min(id) from t_order")
    Long getIdMin();

    @Select("select id from t_order where id>#{lowerIdBoundary} order by id asc limit #{pageSize}")
    Long[] listRangeIds(@Param("lowerIdBoundary") Long lowerIdBoundary, @Param("pageSize") Integer pageSize);
}
