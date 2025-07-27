package com.future.demo.dto;

import lombok.Data;

/**
 * 向随机 id 选择器添加 id 事件
 */
@Data
public class RandomIdPickerAddIdEventDTO {
    private String flag;
    private Long id;
}
