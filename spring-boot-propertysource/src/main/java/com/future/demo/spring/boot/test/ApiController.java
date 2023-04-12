package com.future.demo.spring.boot.test;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Value("${common.property1}")
    String commonProperty1;

    @GetMapping("test1")
    ObjectResponse<String> test1() {
       return ResponseUtils.successObject("common.property1=" + commonProperty1);
    }
}
