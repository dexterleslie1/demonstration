package com.future.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支持逻辑过期时间的对象封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataWithExpiration {
    private LocalDateTime expiration;
    private Object data;
}
