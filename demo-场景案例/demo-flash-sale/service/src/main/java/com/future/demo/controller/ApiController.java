package com.future.demo.controller;

import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.Status;
import com.future.demo.service.OrderService;
import com.future.demo.util.OrderRandomlyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/order")
public class ApiController {
    @Resource
    OrderService orderService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderRandomlyUtil orderRandomlyUtil;

    @GetMapping(value = "create")
    public ObjectResponse<String> create() throws Exception {
        long userId = this.orderRandomlyUtil.getUserIdRandomly();
        long productId = this.orderRandomlyUtil.getProductIdRandomly();
        int amount = 1;
        this.orderService.create(userId, productId, amount);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功下单");
        return response;
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByUserIdAndStatus")
    public ListResponse<OrderDTO> listByUserIdAndStatus() {
        Long userId = this.orderRandomlyUtil.getUserIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByUserIdAndStatus(
                        userId, status, createTime, endTime));
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByUserIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByUserIdAndWithoutStatus() {
        Long userId = this.orderRandomlyUtil.getUserIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        return ResponseUtils.successList(
                this.orderService.listByUserIdAndWithoutStatus(
                        userId, createTime, endTime));
    }
}
