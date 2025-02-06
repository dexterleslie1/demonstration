package com.future.demo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Builder
@Data
public class NetMall {
    private String name;
    private BigDecimal price;

    public BigDecimal getPrice() {
        // 模拟爬取价格网络延迟
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {

        }
        return this.price;
    }
}
