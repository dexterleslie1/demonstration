package com.future.demo.mapdb;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TestDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String name;
    private String code;
    private String description;
    private String status;
    private String remark;
    private String category;
    private String source;

    private BigDecimal amount;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private BigDecimal balance;

    private Integer quantity;
    private Integer count;
    private Integer version;
    private Integer sort;
    private Integer flag;
    private Integer level;

    private Long userId;
    private Long orgId;
    private Long parentId;
    private Long createBy;
    private Long updateBy;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
