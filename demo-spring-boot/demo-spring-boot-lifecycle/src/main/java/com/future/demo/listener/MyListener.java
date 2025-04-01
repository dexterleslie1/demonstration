package com.future.demo.listener;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;

// Spring生命周期监听器
public class MyListener implements SpringApplicationRunListener {
    // 应用正在启动
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        SpringApplicationRunListener.super.starting(bootstrapContext);
    }

    // 环境已经准备好
    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        SpringApplicationRunListener.super.environmentPrepared(bootstrapContext, environment);
    }

    // 上下文已准备
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        SpringApplicationRunListener.super.contextPrepared(context);
    }

    // 上下文已加载
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        SpringApplicationRunListener.super.contextLoaded(context);
    }

    // 应用已启动
    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        SpringApplicationRunListener.super.started(context, timeTaken);
    }

    // 应用已准备就绪
    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        SpringApplicationRunListener.super.ready(context, timeTaken);
    }

    // 应用启动失败
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        SpringApplicationRunListener.super.failed(context, exception);
    }
}
