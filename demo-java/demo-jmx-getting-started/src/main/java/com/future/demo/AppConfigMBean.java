package com.future.demo;

public interface AppConfigMBean {
    // 定义一个属性
    public void setLogLevel(String level);
    public String getLogLevel();

    // 定义一个操作
    public void reloadConfiguration();
}
