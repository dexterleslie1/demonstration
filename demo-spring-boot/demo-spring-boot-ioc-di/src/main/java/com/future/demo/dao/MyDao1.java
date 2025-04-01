package com.future.demo.dao;

import com.future.demo.MyBean7;
import com.future.demo.MyBean8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyDao1 {

    private MyBean7 myBean7;
    private MyBean8 myBean8;

    // spring自动调用此构造函数并注入MyBean7
    public MyDao1(MyBean7 myBean7) {
        this.myBean7 = myBean7;
    }

    public MyBean7 getMyBean7() {
        return myBean7;
    }

    // setter方法注入
    @Autowired
    public void setMyBean8(MyBean8 myBean8) {
        this.myBean8 = myBean8;
    }
    public MyBean8 getMyBean8() {
        return myBean8;
    }
}
