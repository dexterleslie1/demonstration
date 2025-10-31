package com.future.demo;

import com.future.demo.entity.Order;
import com.future.demo.entity.OrderExample;
import com.future.demo.mapper.OrderMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ApplicationTests {

    @Resource
    OrderMapper orderMapper;

    @Test
    void contextLoads() {
        List<Order> orderList = orderMapper.selectByExample(null);
        Assertions.assertEquals(5, orderList.size());

        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andCustomerIdEqualTo(1L);
        orderMapper.selectByExample(orderExample);
    }

}
