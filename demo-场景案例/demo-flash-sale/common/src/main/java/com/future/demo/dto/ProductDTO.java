package com.future.demo.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Integer stock;
    private Long merchantId;
    private boolean flashSale;
    /**
     * 距离秒杀开始时间的秒数
     */
    private long toFlashSaleStartTimeRemainingSeconds;
    /**
     * 距离秒杀结束时间的秒数
     */
    private long toFlashSaleEndTimeRemainingSeconds;
}
