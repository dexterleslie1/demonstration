package com.future.demo;

import lombok.Data;

@Data
public class MessageDTO {
    private String type;
//    // 用于观察在retry过程中是否能够维持消息状态
//    private int statusCount;
}
