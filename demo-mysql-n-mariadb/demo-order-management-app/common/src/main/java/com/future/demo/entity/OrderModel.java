package com.future.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderModel {
    // long 类型
    private Long id;
    // int 类型
    /*private Integer id;*/
    // biginteger 类型
    /*private BigInteger id;*/
    // uuid string 类型
    /*private String id;*/

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
