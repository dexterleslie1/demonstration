package com.future.demo.mapper;

import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    // 自増 long 和 int 类型
    /*@Insert("INSERT INTO t_order (userId, createTime,`status`,deleteStatus) VALUES (#{userId}, #{createTime},#{status},#{deleteStatus})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")*/
    // biginteger 和 uuid string 类型
    @Insert("INSERT INTO t_order (id, userId, createTime,`status`,deleteStatus) VALUES (#{id}, #{userId}, #{createTime},#{status},#{deleteStatus})")
    int insert(OrderModel orderModel);

    // 自増 long 和 int 类型
    /*@Insert("<script>" +
            "   insert into t_order(userId,createTime,`status`,deleteStatus) values " +
            "   <foreach collection=\"orderModelList\" item=\"e\" separator=\",\">" +
            "       (#{e.userId},#{e.createTime},#{e.status},#{e.deleteStatus})" +
            "   </foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")*/
    // biginteger 和 uuid string 类型
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

    @Select("select * from t_order where id=#{orderId}")
        // long 类型
    OrderModel getById(@Param("orderId") Long orderId);
    // int 类型
    /*OrderModel getById(@Param("orderId") Integer orderId);*/
    // biginteger 类型
    /*OrderModel getById(@Param("orderId") BigInteger orderId);*/
    // uuid string 类型
    /*OrderModel getById(@Param("orderId") String orderId);*/

    @Select("select min(id) from t_order")
        // long 类型
    Long getIdMin();
    // int 类型
    /*Integer getIdMin();*/
    // biginteger 类型
    /*BigInteger getIdMin();*/
    // uuid string 类型
    /*String getIdMin();*/

    @Select("select id from t_order where id>#{lowerIdBoundary} order by id asc limit #{pageSize}")
        // long 类型
    Long[] listRangeIds(@Param("lowerIdBoundary") Long lowerIdBoundary, @Param("pageSize") Integer pageSize);
    // int 类型
    /*Integer[] listRangeIds(@Param("lowerIdBoundary") Integer lowerIdBoundary, @Param("pageSize") Integer pageSize);*/
    // biginteger 类型
    /*BigInteger[] listRangeIds(@Param("lowerIdBoundary") BigInteger lowerIdBoundary, @Param("pageSize") Integer pageSize);*/
    // uuid string 类型
    /*String[] listRangeIds(@Param("lowerIdBoundary") String lowerIdBoundary, @Param("pageSize") Integer pageSize);*/
}
