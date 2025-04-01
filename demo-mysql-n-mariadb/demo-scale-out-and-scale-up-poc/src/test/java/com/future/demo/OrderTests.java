package com.future.demo;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.DeleteStatus;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import com.future.demo.mapper.OrderMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
public class OrderTests {

    @Resource
    OrderMapper orderMapper;

    @Test
    public void test() {
        Long id = IdUtil.getSnowflake().nextId();

        Order order = new Order();
        order.setId(id);
        order.setCreateTime(LocalDateTime.now());
        order.setUserId(RandomUtil.randomLong());
        order.setMerchantId(RandomUtil.randomLong());
        order.setTotalAmount(new BigDecimal(1000));
        order.setTotalCount(10);
        order.setStatus(Status.Unpay);
        order.setDeleteStatus(DeleteStatus.Normal);
        this.orderMapper.add(order);

        order = this.orderMapper.get(id);
        Assertions.assertEquals(id, order.getId());
        Assertions.assertEquals(Status.Unpay, order.getStatus());
    }
}
