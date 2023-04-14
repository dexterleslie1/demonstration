package com.future.demo.mockito;

import org.springframework.stereotype.Service;

@Service
public class MyServiceInner {

    public String test1(String param1) {
        return "param1=" + param1;
    }

    public String test2(String param2)  {
        return "param2=" + param2;
    }

}
