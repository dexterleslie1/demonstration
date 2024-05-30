package com.future.demo;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MyService {

    @Resource
    MyServiceInner myServiceInner;

    public String test1(String param1) {
       return this.myServiceInner.test1(param1);
    }

    public String test2(String param2) throws Exception {
        return this.myServiceInner.test2(param2);
    }

}
