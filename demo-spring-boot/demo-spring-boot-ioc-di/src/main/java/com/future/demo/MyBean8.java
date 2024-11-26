package com.future.demo;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// 通过EnvironmentAware和BeanNameAware接口，实现对beanName和环境变量的注入
@Component
public class MyBean8 implements EnvironmentAware, BeanNameAware {
    private String beanName;
    private Environment env;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getOsType() {
        return env.getProperty("os.name");
    }
}
