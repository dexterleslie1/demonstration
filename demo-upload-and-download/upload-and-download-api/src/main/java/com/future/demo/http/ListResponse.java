package com.future.demo.http;

import java.util.List;

/**
 * List对象返回
 */
public class ListResponse<T> extends BaseResponse {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
