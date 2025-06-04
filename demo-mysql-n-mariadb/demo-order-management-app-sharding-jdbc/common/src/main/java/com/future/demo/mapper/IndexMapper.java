package com.future.demo.mapper;

import com.future.demo.entity.OrderIndexListByUserIdModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndexMapper {
    @Insert("<script>" +
            "   insert into t_order_index_list_by_userid(id,userId,`status`,deleteStatus,createTime,orderId) values " +
            "   <foreach collection=\"list\" item=\"e\" separator=\",\">" +
            "       (#{e.id},#{e.userId},#{e.status},#{e.deleteStatus},#{e.createTime},#{e.orderId})" +
            "   </foreach>" +
            "</script>")
    void insertBatchOrderIndexListByUserId(@Param("list") List<OrderIndexListByUserIdModel> orderIndexListByUserIdModelList);
}
