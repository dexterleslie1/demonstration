package com.future.demo.mapper;

import com.future.demo.bean.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

}
