package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.DeleteStatus;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import com.future.demo.mapper.OrderMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootTest
public class OrderTests {

    @Resource
    OrderMapper orderMapper;

    @Test
    public void test() {
        this.orderMapper.truncate();

        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            order.setCreateTime(now);
            order.setUserId(RandomUtil.randomLong());
            order.setMerchantId(RandomUtil.randomLong());
            order.setTotalAmount(new BigDecimal(1000));
            order.setTotalCount(10);
            order.setStatus(Status.Unpay);
            order.setDeleteStatus(DeleteStatus.Normal);
            this.orderMapper.add(order);

            Long id = order.getId();

            order = this.orderMapper.get(id);
            Assertions.assertEquals(id, order.getId());
            Assertions.assertEquals(Status.Unpay, order.getStatus());
            Assertions.assertEquals(now, order.getCreateTime());
        }
    }
}
