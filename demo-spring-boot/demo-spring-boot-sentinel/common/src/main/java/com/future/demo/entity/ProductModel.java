package com.future.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.future.demo.dto.ProductDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class ProductModel {
    private Long id;
    private String name;
    private Integer stock;
    private Long merchantId;
    private boolean flashSale;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime flashSaleStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime flashSaleEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 转换为 DTO 对象
     *
     * @return
     */
    public ProductDTO toDTO() {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(this, dto);
        dto.setId(String.valueOf(this.id));
        if (this.merchantId != null)
            dto.setMerchantId(String.valueOf(this.merchantId));
        if (dto.isFlashSale()) {
            // 秒杀类型商品
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            LocalDateTime flashSaleStartTime = this.getFlashSaleStartTime();
            dto.setToFlashSaleStartTimeRemainingSeconds(Duration.between(localDateTimeNow, flashSaleStartTime).toSeconds());

            LocalDateTime flashSaleEndTime = this.getFlashSaleEndTime();
            dto.setToFlashSaleEndTimeRemainingSeconds(Duration.between(localDateTimeNow, flashSaleEndTime).toSeconds());
        }
        return dto;
    }
}
