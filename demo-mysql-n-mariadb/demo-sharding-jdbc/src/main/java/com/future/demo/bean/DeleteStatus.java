package com.future.demo.bean;

public enum DeleteStatus {
    Normal("正常"),
    Deleted("已删除");
    private String description;

    DeleteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
