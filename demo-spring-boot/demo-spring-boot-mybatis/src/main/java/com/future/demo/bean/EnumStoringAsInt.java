package com.future.demo.bean;

import lombok.Data;

/**
 * MySQL 使用 int 存储枚举
 */
@Data
public class EnumStoringAsInt {
    private long id;
    private int status;
}
