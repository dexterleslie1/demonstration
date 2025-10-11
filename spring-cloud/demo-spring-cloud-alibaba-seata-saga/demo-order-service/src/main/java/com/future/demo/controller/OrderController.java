package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.entity.Order;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * @param userId
     * @param productId
     * @param count
     * @param amount
     * @param throwBusinessException 是否抛出业务异常
     * @return
     * @throws BusinessException
     */
    @PostMapping("/order/create")
    ObjectResponse<Long> createOrder(@RequestParam("userId") Long userId,
                                     @RequestParam("productId") Long productId,
                                     @RequestParam("count") Integer count,
                                     @RequestParam("amount") BigDecimal amount,
                                     @RequestParam(value = "throwBusinessException", defaultValue = "false") boolean throwBusinessException) throws BusinessException {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setCount(count);
        order.setMoney(amount);
        Long orderId = this.orderService.createOrder(order, throwBusinessException);
        return ResponseUtils.successObject(orderId);
    }
}
