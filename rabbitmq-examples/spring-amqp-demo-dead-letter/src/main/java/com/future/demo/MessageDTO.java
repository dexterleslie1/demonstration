package com.future.demo;

import lombok.Data;

@Data
public class MessageDTO {
    private String type;
    private int retries;
}
