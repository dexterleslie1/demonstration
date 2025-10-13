package com.future.demo.entity;

import lombok.Data;

@Data
public class StorageFreeze {
    private String xid;
    private Long productId;
    private Integer freezeStock;
}
