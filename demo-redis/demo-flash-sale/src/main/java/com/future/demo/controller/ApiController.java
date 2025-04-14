package com.future.demo.controller;

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
        String dbSyncStatusId = null;
        dbSyncStatusId = this.orderService.createOrderBasedRedisWithLuaScript(userId, productId, amount);
        /*this.orderService.createOrderBasedDB(userId, productId, amount);*/
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(dbSyncStatusId);
        return response;
    }

    /**
     * 秒杀成功后，查询订单数据库同步状态，以实现页面跳转功能
     *
     * @param dbSyncStatusId
     * @return
     */
    @GetMapping(value = "checkDbSyncStatus")
    public ObjectResponse<String> checkDbSyncStatus(
            @RequestParam(value = "dbSyncStatusId") String dbSyncStatusId) {
        String status = this.redisTemplate.opsForValue().get(dbSyncStatusId);
        return ResponseUtils.successObject(status);
    }

    /**
     * 根据用户 ID 查询订单列表
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "listOrders")
    public ListResponse<OrderDTO> listOrders(
            @RequestParam(value = "userId") Long userId) {
        return ResponseUtils.successList(this.orderService.list(userId));
    }
}
