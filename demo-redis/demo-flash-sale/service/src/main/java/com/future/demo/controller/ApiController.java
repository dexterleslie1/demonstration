package com.future.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    final static Random RANDOM = new Random(System.currentTimeMillis());
    @Resource
    OrderService orderService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @PostMapping(value = "purchaseProduct")
    public ObjectResponse<String> purchaseProduct(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "productId") Long productId,
            @RequestParam(value = "amount") Integer amount) throws Exception {
        userId = RANDOM.nextInt(OrderService.UserCount) + 1L;
        productId = RANDOM.nextInt(OrderService.ProductCount) + 1L;
        amount = 1;
        this.orderService.createOrderBasedRedisWithLuaScript(userId, productId, amount);
        /*this.orderService.createOrderBasedDB(userId, productId, amount);*/
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功下单");
        return response;
    }

    /**
     * 根据用户 ID 查询订单列表
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "listOrders")
    public ListResponse<OrderDTO> listOrders(
            @RequestParam(value = "userId") Long userId) throws JsonProcessingException {
        return ResponseUtils.successList(this.orderService.list(userId));
    }
}
