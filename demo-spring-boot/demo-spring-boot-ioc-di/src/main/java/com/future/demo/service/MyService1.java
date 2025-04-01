package com.future.demo.service;

import com.future.demo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MyService1 {
    // region @Autowired测试

    // 根据类型@Autowired注入
    @Autowired
    public MyBean2 xxx;

    // 先根据类型@Autowired注入，再根据名称@Autowired注入，如果没有找到，则抛出异常
    @Autowired
    public MyBean4 myBean41;

    // 根据类型注入多个bean
    @Autowired
    public List<MyBean1> myBean1List;

    // 根据类型注入多个bean
    @Autowired
    public Map<String, MyBean1> myBean1Map;

    // 注入spring ioc容器
    @Autowired
    public ApplicationContext ioc;

    // endregion

    // 因为有@Primary注解指定myBean61为默认的bean，所以使用@Qualifier注解指定myBean62以忽略myBean61的@Primary注解
    @Qualifier("myBean62")
    @Autowired
    public MyBean6 xxx2;
}
