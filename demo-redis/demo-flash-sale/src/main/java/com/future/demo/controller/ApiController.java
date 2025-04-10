package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    final static Random RANDOM = new Random(System.currentTimeMillis());
    @Resource
    OrderService orderService;

    @PostMapping(value = "purchaseProduct")
    public ObjectResponse<String> purchaseProduct(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "productId") Long productId,
            @RequestParam(value = "amount") Integer amount) throws Exception {
        userId = RANDOM.nextLong();
        productId = RANDOM.nextInt(OrderService.ProductCount) + 1L;
        amount = 1;
        this.orderService.createOrderBasedRedisWithLuaScript(userId, productId, amount);
        /*this.orderService.createOrderBasedDB(userId, productId, amount);*/
        String uuidStr = UUID.randomUUID().toString();
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(uuidStr);
        return response;
    }
}
