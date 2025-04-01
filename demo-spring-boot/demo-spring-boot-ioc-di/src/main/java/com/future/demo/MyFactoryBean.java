package com.future.demo;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

// 使用场景：用于创建比较复杂的对象，比如需要依赖其他对象的创建
@Component
public class MyFactoryBean implements FactoryBean<MyBean5> {
    @Override
    public MyBean5 getObject() throws Exception {
        return new MyBean5();
    }

    @Override
    public Class<?> getObjectType() {
        return MyBean5.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
