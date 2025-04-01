package com.future.demo.bean;

import lombok.Data;

import java.util.List;

@Data
public class Customer {
    private Long id;
    private String customerName;
    private String phone;

    /**
     * 客户的订单列表
     */
    private List<Order> orders;
}
