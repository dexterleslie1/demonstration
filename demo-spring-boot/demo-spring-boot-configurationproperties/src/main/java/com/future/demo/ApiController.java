package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    // 注入并使用自定义属性
    @Resource
    MyProperties myProperties;

    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject("p1=" + this.myProperties.getP1() + ",p2=" + this.myProperties.getP2());
    }
}
