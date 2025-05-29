package com.future.demo.controller;

import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.Status;
import com.future.demo.service.IdCacheAssistantService;
import com.future.demo.service.OrderService;
import com.future.demo.util.OrderRandomlyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    IdCacheAssistantService idCacheAssistantService;

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
        this.orderService.createOrder(userId, productId, amount);
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
        Long orderId = this.idCacheAssistantService.getRandomly();
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

    /**
     * 批量初始化订单数据
     *
     * @return
     */
    @GetMapping(value = "initInsertBatch")
    public ObjectResponse<String> initInsertBatch() {
        this.orderService.insertBatch();
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功批量初始化订单");
        return response;
    }

    /**
     * 初始化id缓存辅助数据
     *
     * @return
     */
    @GetMapping(value = "init")
    public ObjectResponse<String> init() {
        this.idCacheAssistantService.initData();
        return ResponseUtils.successObject("成功初始化");
    }
}
