package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.demo.entity.Order;
import com.future.demo.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
public class OrderController {
    @Resource
    OrderService orderService;

    @PostMapping("/order/create")
    ObjectResponse<String> createOrder(@RequestParam("userId") Long userId,
                                       @RequestParam("productId") Long productId,
                                       @RequestParam(value = "count") Integer count,
                                       @RequestParam(name = "amount") BigDecimal amount) throws BusinessException {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setCount(count);
        order.setMoney(amount);
        this.orderService.createOrder(order);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("订单创建成功");
        return response;
    }
}
