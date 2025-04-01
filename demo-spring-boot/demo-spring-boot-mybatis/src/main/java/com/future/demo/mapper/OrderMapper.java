package com.future.demo.mapper;

import com.future.demo.bean.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {
    /**
     * 根据订单id查询订单信息并且同时查询订单对应的客户信息
     */
    Order findByIdWithCustomer(@Param("id") Long id);

    /**
     * 根据订单id查询订单信息并且同时查询订单对应的客户信息，使用分步查询
     * @param id
     * @return
     */
    Order findByIdWithCustomerStep(@Param("id") Long id);

    @Insert("insert into `order`(address,amount,customer_id,create_time) values(#{address},#{amount},#{customerId},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(Order order);

    @Delete("delete from `order` where id=#{id}")
    void delete(long id);
}
