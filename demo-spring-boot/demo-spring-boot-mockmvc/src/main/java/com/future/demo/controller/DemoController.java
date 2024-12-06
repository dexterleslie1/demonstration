package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.bean.MyBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @RequestMapping("/")
    public ObjectResponse<MyBean> index() {
        MyBean myBean = new MyBean();
        myBean.setName("张三");
        myBean.setAge(18);
        ObjectResponse<MyBean> response = new ObjectResponse<>();
        response.setData(myBean);
        return response;
    }
}
