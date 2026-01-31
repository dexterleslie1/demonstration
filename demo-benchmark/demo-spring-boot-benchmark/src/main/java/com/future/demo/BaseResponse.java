package com.future.demo;

import lombok.Data;

@Data
public abstract class BaseResponse {
    private int errorCode = 0;
    private String errorMessage = null;
}

