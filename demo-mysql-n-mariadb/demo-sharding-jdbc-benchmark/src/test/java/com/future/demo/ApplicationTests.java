package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class ApplicationTests {

    final static String StrConstOrder = "order";

    @Resource
    OrderMapper orderMapper;
    @Autowired
    SnowflakeService snowflakeService;

    @Test
    public void contextLoads() {
        int totalCount = 15;

        this.orderMapper.truncate();

        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            Order order = new Order();
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            order.setCreateTime(now);
            Long userId = RandomUtil.randomLong(1, Long.MAX_VALUE);
            order.setUserId(userId);
            order.setMerchantId(RandomUtil.randomLong(1, Long.MAX_VALUE));
            order.setTotalAmount(new BigDecimal(1000));
            order.setTotalCount(10);
            Status status = OrderRandomlyUtil.getStatusRandomly();
            order.setStatus(status);
            order.setDeleteStatus(OrderRandomlyUtil.getDeleteStatusRandomly());
            Long snowflakeId = this.snowflakeService.getId(StrConstOrder).getId();
            OrderRandomlyUtil.injectUserIdGenIntoOrderId(snowflakeId, userId, order);
            this.orderMapper.add(order);
            orderList.add(order);

            Long id = order.getId();

            order = this.orderMapper.get(id);
            Assertions.assertEquals(id, order.getId());
            Assertions.assertEquals(status, order.getStatus());
            Assertions.assertEquals(now, order.getCreateTime());
        }

        Map<Long, Order> idToOrderMap = orderList.stream().collect(Collectors.toMap(Order::getId, o -> o));
        List<Order> orderListResult = this.orderMapper.listById(orderList.stream().map(Order::getId).collect(Collectors.toList()));
        Assertions.assertEquals(orderList.size(), orderListResult.size());
        Map<Long, Order> finalIdToOrderMap1 = idToOrderMap;
        orderListResult.forEach(o -> {
            Assertions.assertEquals(finalIdToOrderMap1.get(o.getId()).getCreateTime(), o.getCreateTime());
            Assertions.assertEquals(finalIdToOrderMap1.get(o.getId()).getUserId(), o.getUserId());
            Assertions.assertEquals(finalIdToOrderMap1.get(o.getId()).getMerchantId(), o.getMerchantId());
        });

        // region 测试批量插入
        this.orderMapper.truncate();

        orderList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            Order order = new Order();
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            order.setCreateTime(now);
            Long userId = RandomUtil.randomLong(1, Long.MAX_VALUE);
            order.setUserId(userId);
            order.setMerchantId(RandomUtil.randomLong(1, Long.MAX_VALUE));
            order.setTotalAmount(new BigDecimal(1000));
            order.setTotalCount(10);
            order.setStatus(OrderRandomlyUtil.getStatusRandomly());
            order.setDeleteStatus(OrderRandomlyUtil.getDeleteStatusRandomly());
            Long snowflakeId = this.snowflakeService.getId(StrConstOrder).getId();
            OrderRandomlyUtil.injectUserIdGenIntoOrderId(snowflakeId, userId, order);
            orderList.add(order);
        }
        this.orderMapper.addBatch(orderList);

        orderList = this.orderMapper.listAll();
        idToOrderMap = orderList.stream().collect(Collectors.toMap(Order::getId, o -> o));
        orderListResult = this.orderMapper.listById(orderList.stream().map(Order::getId).collect(Collectors.toList()));
        Assertions.assertEquals(orderList.size(), orderListResult.size());
        Map<Long, Order> finalIdToOrderMap = idToOrderMap;
        orderListResult.forEach(o -> {
            Assertions.assertEquals(finalIdToOrderMap.get(o.getId()).getCreateTime(), o.getCreateTime());
            Assertions.assertEquals(finalIdToOrderMap.get(o.getId()).getUserId(), o.getUserId());
            Assertions.assertEquals(finalIdToOrderMap.get(o.getId()).getMerchantId(), o.getMerchantId());
        });

        // endregion

        // region 综合查询测试

        orderListResult = this.orderMapper.listAll();

        // 根据用户ID查询
        Order expectedOrder = orderListResult.get(RandomUtil.randomInt(0, orderListResult.size()));
        orderList = this.orderMapper.listByUserId(expectedOrder.getUserId(),
                expectedOrder.getStatus(), expectedOrder.getDeleteStatus(), null, null, 0L, 10L);
        Assertions.assertEquals(1, orderList.size());
        Assertions.assertEquals(expectedOrder.getId(), orderList.get(0).getId());

        // 根据商家ID查询
        expectedOrder = orderListResult.get(RandomUtil.randomInt(0, orderListResult.size()));
        orderList = this.orderMapper.listByMerchantId(expectedOrder.getMerchantId(),
                expectedOrder.getStatus(), expectedOrder.getDeleteStatus(), null, null, 0L, 10L);
        Assertions.assertEquals(1, orderList.size());
        Assertions.assertEquals(expectedOrder.getId(), orderList.get(0).getId());

        // endregion
    }
}
