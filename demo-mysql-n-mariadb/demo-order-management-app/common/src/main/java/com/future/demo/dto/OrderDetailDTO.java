package com.future.demo.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long id;

    // long 类型
    /*private Long orderId;*/
    // int 类型
    /*private Integer orderId;*/
    // biginteger 类型
    /*private BigInteger orderId;*/
    // uuid string 类型
    private String orderId;

    private Long productId;
    private Long merchantId;
    private Integer amount;
}
