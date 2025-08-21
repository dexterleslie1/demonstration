package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderModel {
    /*private BigInteger id;*/
    private Long id;
    private Long userId;
    private LocalDateTime createTime;
    private Status status;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receivedTime;
    private LocalDateTime cancelTime;
    private DeleteStatus deleteStatus;

    private List<OrderDetailModel> orderDetailList;
}
