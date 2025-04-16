package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderModel {
    private Long id;
    private Long userId;
    private LocalDateTime createTime;

    private List<OrderDetailModel> orderDetailList;
}
