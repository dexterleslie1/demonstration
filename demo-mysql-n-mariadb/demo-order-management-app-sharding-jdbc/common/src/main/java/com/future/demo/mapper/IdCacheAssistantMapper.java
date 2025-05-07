package com.future.demo.mapper;

import com.future.demo.entity.IdCacheAssistantEntity;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface IdCacheAssistantMapper {
    @Insert("<script>" +
            "insert into t_id_cache_assistant(orderId) values " +
            "   <foreach item=\"e\" collection=\"orderList\" separator=\",\">" +
            "       (#{e.orderId})" +
            "   </foreach>" +
            "</script>")
    void addBatch(@Param("orderList") List<IdCacheAssistantEntity> idCacheAssistantEntityList);

    @Select("select min(id) from t_id_cache_assistant")
    Long getIdMin();

    @Select("select max(id) from t_id_cache_assistant")
    Long getIdMax();

    @Select("select orderId from t_id_cache_assistant where id>=#{idLowerBoundary} order by id asc limit #{pageSize}")
    BigInteger[] listOrderId(@Param("idLowerBoundary") long idLowerBoundary,
                             @Param("pageSize") int pageSize);

    @Update("truncate table t_id_cache_assistant")
    void truncate();
}
