package com.future.demo;

import lombok.Data;

@Data
public class ObjectResponse<T> extends BaseResponse {
    private T data;
}
