package com.future.demo.controller;

import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.Status;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.service.OrderService;
import com.future.demo.util.OrderRandomlyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/order")
@Slf4j
public class OrderController {
    @Resource
    OrderService orderService;
    @Resource
    OrderRandomlyUtil orderRandomlyUtil;
    @Resource
    OrderMapper orderMapper;

    private int orderIdMax = 0;

    @PostConstruct
    public void init() {
        Long orderIdMaxLong = this.orderMapper.getOrderIdMax();
        if (orderIdMaxLong != null) {
            this.orderIdMax = orderIdMaxLong.intValue();
        }
        log.info("成功加载最大订单ID值{}", this.orderIdMax);
    }

    /**
     * 创建订单
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "create")
    public ObjectResponse<String> create() throws Exception {
        Long userId = this.orderRandomlyUtil.getUserIdRandomly();
        Long productId = this.orderRandomlyUtil.getProductIdRandomly();
        Integer amount = 1;
        this.orderService.createOrderBasedDB(userId, productId, amount);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功创建订单");
        return response;
    }

    /**
     * 根据订单 ID 查询订单信息
     *
     * @return
     */
    @GetMapping(value = "getById")
    public ObjectResponse<OrderDTO> getById() {
        long orderId = RandomUtils.nextInt(this.orderIdMax) + 1;
        OrderDTO orderDTO = this.orderService.getById(orderId);
        return ResponseUtils.successObject(orderDTO);
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
     * 商家查询指定日期范围+所有状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndWithoutStatus() {
        Long merchantId = this.orderRandomlyUtil.getMerchantIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByMerchantIdAndWithoutStatus(
                        merchantId, deleteStatus, createTime, endTime));
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndStatus() {
        Long merchantId = this.orderRandomlyUtil.getMerchantIdRandomly();
        LocalDateTime createTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = createTime.plusMonths(1);
        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
        Status status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(
                this.orderService.listByMerchantIdAndStatus(
                        merchantId, status, deleteStatus, createTime, endTime));
    }
}
