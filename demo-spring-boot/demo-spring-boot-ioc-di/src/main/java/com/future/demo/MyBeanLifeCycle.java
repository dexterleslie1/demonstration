package com.future.demo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

// 测试bean生命周期方法
public class MyBeanLifeCycle implements InitializingBean, DisposableBean {
    boolean isInit = false;
    boolean invokedAfterPropertiesSet = false;
    boolean invokedPostConstruct = false;

    public void initMethod() {
        isInit = true;
    }

    public void destroyMethod() {
        isInit = false;
    }

    @Override
    public void destroy() throws Exception {
        // 调用destroyMethod方法之前被调用
    }

    // 属性设置完成后被调用
    @Override
    public void afterPropertiesSet() throws Exception {
        // @Autowired完成后被调用
        // 调用initMethod方法之前被调用
        invokedAfterPropertiesSet = true;
    }

    @PostConstruct
    public void postConstruct() {
        // 在构造函数和@Autowired之后被调用
        // 在afterPropertiesSet之前被调用
        invokedPostConstruct = true;
    }

    @PreDestroy
    public void preDestroy() {
        // 在destroy方法之前被调用
    }
}
