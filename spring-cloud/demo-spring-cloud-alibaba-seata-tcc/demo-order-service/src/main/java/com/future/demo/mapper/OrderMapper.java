package com.future.demo.mapper;

import com.future.demo.entity.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into t_order(user_id, product_id, count, money, status) values (#{userId}, #{productId}, #{count}, #{money}, #{status})")
    int insert(Order order);

    @Select("select * from t_order where id = #{id}")
    Order findById(Long id);

    @Update("update t_order set status = #{status} where id = #{id}")
    int update(Order order);

    /**
     * 协助测试重置数据
     *
     * @return
     */
    @Delete("delete from `t_order`")
    void reset();
}
