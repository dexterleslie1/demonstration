package com.future.demo;


public class ObjectResponse<T> extends BaseResponse {
    private T data;

    public ObjectResponse() {
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
