package com.future.demo.controller;

import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.Status;
import com.future.demo.service.MerchantService;
import com.future.demo.service.OrderService;
import com.future.demo.service.UserService;
import com.future.demo.util.OrderRandomlyUtil;
import com.future.random.id.picker.RandomIdPickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/order")
@Slf4j
public class OrderController {
    @Resource
    OrderService orderService;
    @Resource
    RandomIdPickerService randomIdPickerService;
    @Resource
    UserService userService;
    @Resource
    MerchantService merchantService;

    /**
     * 根据订单 ID 查询订单信息
     *
     * @return
     */
    @GetMapping(value = "getById")
    public ObjectResponse<OrderDTO> getById() throws IOException {
        Long orderId = Long.parseLong(randomIdPickerService.getIdRandomly("order"));
        return ResponseUtils.successObject(this.orderService.getById(orderId));
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByUserIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByUserIdAndWithoutStatus() throws IOException {
        Long userId = userService.getIdRandomly();
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime createTime = endTime.minusMonths(1);
        return ResponseUtils.successList(this.orderService.listByUserIdAndWithoutStatus(
                userId, createTime, endTime));
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByUserIdAndStatus")
    public ListResponse<OrderDTO> listByUserIdAndStatus() throws IOException {
        Long userId = userService.getIdRandomly();
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime createTime = endTime.minusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(this.orderService.listByUserIdAndStatus(
                userId, status, createTime, endTime));
    }

    /**
     * 商家查询指定日期范围+所有状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndWithoutStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndWithoutStatus() throws IOException {
        Long merchantId = merchantService.getIdRandomly();
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime createTime = endTime.minusMonths(1);
        return ResponseUtils.successList(this.orderService.listByMerchantIdAndWithoutStatus(
                merchantId, createTime, endTime));
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @return
     */
    @GetMapping(value = "listByMerchantIdAndStatus")
    public ListResponse<OrderDTO> listByMerchantIdAndStatus() throws IOException {
        Long merchantId = merchantService.getIdRandomly();
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime createTime = endTime.minusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        return ResponseUtils.successList(this.orderService.listByMerchantIdAndStatus(
                merchantId, status, createTime, endTime));
    }

    /**
     * 根据中文关键词搜索订单
     *
     * @return
     */
    @GetMapping(value = "listByKeyword")
    public ListResponse<OrderDTO> listByKeyword(@RequestParam(value = "keyword", defaultValue = "") String keyword) throws IOException {
        return ResponseUtils.successList(this.orderService.listByKeyword(keyword));
    }

    /**
     * 批量初始化订单数据
     *
     * @return
     */
    @GetMapping(value = "initInsertBatch")
    public ObjectResponse<String> initInsertBatch() throws IOException {
        this.orderService.insertBatch(null, null, null, null, 0);
        return ResponseUtils.successObject("成功批量初始化订单");
    }
}
