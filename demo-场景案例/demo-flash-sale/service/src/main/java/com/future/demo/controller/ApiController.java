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
@RequestMapping("/api/v1/order")
public class ApiController {
    final static Random RANDOM = new Random(System.currentTimeMillis());
    @Resource
    OrderService orderService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping(value = "create")
    public ObjectResponse<String> create() throws Exception {
        int totalProductCount = this.orderService.getTotalProductCount();
        long userId = RANDOM.nextInt(OrderService.UserCount) + 1L;
        long productId = RANDOM.nextInt(totalProductCount) + 1L;
        int amount = 1;
        this.orderService.create(userId, productId, amount);
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
