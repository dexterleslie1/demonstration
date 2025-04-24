package com.future.demo.mapper;

import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO t_order (userId, createTime,`status`,deleteStatus) VALUES (#{userId}, #{createTime},#{status},#{deleteStatus})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(OrderModel orderModel);

    @Delete("DELETE FROM t_order")
    void deleteAll();

    @Select("SELECT * FROM t_order")
    List<OrderModel> selectAll();

    @Select("select distinct id from t_order")
    List<Long> selectAllId();

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

    @Select("select * from t_order where id=#{orderId}")
    OrderModel getById(@Param("orderId") Long orderId);
}
