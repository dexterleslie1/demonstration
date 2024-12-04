package com.future.demo.mapper;

import com.future.demo.bean.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper {

    // 根据id查询客户，并且查询出客户关联的订单
    Customer findByIdWithOrders(@Param("id") Long id);

    // 根据id查询客户，并且查询出客户关联的订单，使用分步查询方法
    Customer findByIdWithOrdersStep(@Param("id") Long id);
}
