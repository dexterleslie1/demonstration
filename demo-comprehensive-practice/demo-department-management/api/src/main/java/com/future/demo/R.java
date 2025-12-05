package com.future.demo;

import lombok.Data;

/**
 * Api响应类
 */
@Data
public class R {
    private int errorCode;
    private String errorMessage;
    private Object data;

    public static R success(Object data) {
        R r = new R();
        r.setData(data);
        return r;
    }

    public static R success() {
        return success(null);
    }
}
