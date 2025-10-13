package com.future.demo.entity;

import lombok.Data;

@Data
public class AccountFreeze {
    private String xid;
    private Long userId;
    private Integer freezeMoney;
}
