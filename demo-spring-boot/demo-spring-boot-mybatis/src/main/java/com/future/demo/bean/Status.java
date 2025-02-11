package com.future.demo.bean;

public enum Status {

    Create("创建"),
    Paying("支付中"),
    InProgress("支付中"),
    Failed("支付失败"),
    Reversed("取消订单");

    private String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
