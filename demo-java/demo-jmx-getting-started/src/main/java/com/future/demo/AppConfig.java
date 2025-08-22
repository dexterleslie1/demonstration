package com.future.demo;

public class AppConfig implements AppConfigMBean {
    private String logLevel = "INFO";

    @Override
    public String getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(String level) {
        this.logLevel = level;
        System.out.println("LogLevel changed to: " + level);
        // 这里可以实际触发日志框架重新加载配置
    }

    @Override
    public void reloadConfiguration() {
        System.out.println("Configuration reloaded with logLevel: " + logLevel);
    }
}
