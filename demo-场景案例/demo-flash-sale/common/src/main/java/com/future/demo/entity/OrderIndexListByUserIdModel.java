package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderIndexListByUserIdModel {
    private Long userId;
    private Status status;
    private LocalDateTime createTime;
    private Long orderId;
    private Long merchantId;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receivedTime;
    private LocalDateTime cancelTime;
    private List<OrderIndexListByUserIdDetailModel> detailModelList;

    @Data
    public static class OrderIndexListByUserIdDetailModel {
        private Long productId;
        private Integer amount;
    }
}
