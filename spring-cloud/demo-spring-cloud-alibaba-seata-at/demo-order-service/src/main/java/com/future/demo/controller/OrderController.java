package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.entity.Order;
import com.future.demo.feign.AccountClient;
import com.future.demo.feign.StorageClient;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@RestController
public class OrderController {
    @Resource
    OrderService orderService;
    @Autowired
    OrderMapper orderMapper;
    @Resource
    StorageClient storageClient;
    @Resource
    AccountClient accountClient;

    /**
     * @param userId
     * @param productId
     * @param count
     * @param amount
     * @return
     * @throws BusinessException
     */
    @PostMapping("/order/create")
    ObjectResponse<Long> createOrder(@RequestParam("userId") Long userId,
                                     @RequestParam("productId") Long productId,
                                     @RequestParam("count") Integer count,
                                     @RequestParam("amount") BigDecimal amount) throws BusinessException {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setCount(count);
        order.setMoney(amount);
        Long orderId = this.orderService.createOrder(order);
        return ResponseUtils.successObject(orderId);
    }

    /**
     * 协助测试重置数据
     *
     * @return
     */
    @GetMapping("/order/reset")
    ObjectResponse<String> reset() throws BusinessException {
        orderMapper.reset();
        storageClient.reset();
        accountClient.reset();
        return ResponseUtils.successObject("重置成功");
    }
}
