package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.Status;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;

    @Resource
    OrderService orderService;

    @Test
    public void test() throws Exception {
        // 还原测试数据
        this.reset();

        // region 测试订单 getById

        this.orderService.insertBatch();

        List<OrderModel> orderModelList = this.orderMapper.selectAll();

        BigDecimal orderId = orderModelList.get(0).getId();

        OrderDTO orderDTO = this.orderService.getById(orderId);
        Assertions.assertEquals(orderId, orderDTO.getId());
        Assertions.assertEquals(orderModelList.get(0).getUserId(), orderDTO.getUserId());
        Assertions.assertEquals(orderModelList.get(0).getStatus(), orderDTO.getStatus());
        Assertions.assertEquals(orderModelList.get(0).getDeleteStatus(), orderDTO.getDeleteStatus());
        List<OrderDetailModel> orderDetailModelList = this.orderDetailMapper.list(Collections.singletonList(orderId));
        Assertions.assertEquals(orderDetailModelList.size(), orderDTO.getOrderDetailList().size());
        Assertions.assertEquals(orderDetailModelList.get(0).getId(), orderDTO.getOrderDetailList().get(0).getId());

        // endregion

        // region 测试 listByUserIdAndStatus

        OrderModel orderModel = orderModelList.get(0);
        Long userId = orderModel.getUserId();
        Status status = orderModel.getStatus();
        LocalDateTime startTime = orderModel.getCreateTime().minusMonths(1);
        LocalDateTime endTime = orderModel.getCreateTime().plusMonths(1);
        List<OrderDTO> orderDTOList = this.orderService.listByUserIdAndStatus(userId, status, startTime, endTime);
        Assertions.assertFalse(orderDTOList.isEmpty());
        Assertions.assertEquals(orderModel.getId(), orderDTOList.get(0).getId());

        // endregion

        // region 测试 listByUserIdAndWithoutStatus

        orderModel = orderModelList.get(0);
        userId = orderModel.getUserId();
        startTime = orderModel.getCreateTime().minusMonths(1);
        endTime = orderModel.getCreateTime().plusMonths(1);
        orderDTOList = this.orderService.listByUserIdAndWithoutStatus(userId, startTime, endTime);
        Assertions.assertFalse(orderDTOList.isEmpty());
        Assertions.assertEquals(orderModel.getId(), orderDTOList.get(0).getId());

        // endregion
    }

    void reset() throws BusinessException {
        // 删除所有订单
        this.orderDetailMapper.truncate();
        this.orderMapper.truncate();
        // 还原商品库存
        this.orderService.restoreProductStock();
    }
}
