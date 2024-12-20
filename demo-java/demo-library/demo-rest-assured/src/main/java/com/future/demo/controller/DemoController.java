package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.bean.MyBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/test1")
    public ObjectResponse<MyBean> test1() {
        ObjectResponse<MyBean> response = new ObjectResponse<>();
        MyBean bean = new MyBean();
        bean.setField1("field1");
        bean.setField2("field2");
        response.setData(bean);
        return response;
    }
}
