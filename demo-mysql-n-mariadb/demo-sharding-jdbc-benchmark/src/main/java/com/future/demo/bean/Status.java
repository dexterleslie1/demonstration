package com.future.demo.bean;

public enum Status {

    Unpay("未支付"),
    Undelivery("未发货"),
    Unreceive("未收货"),
    Received("已签收"),
    Canceled("买家取消");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
