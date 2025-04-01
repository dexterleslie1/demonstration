package com.future.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// 测试@RequestMapping注解的限制
@Controller
public class RequestMappingLimitationController {
    // 限制请求方法，只允许POST请求
    @ResponseBody
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public String test1() {
        return "test1";
    }

    // 限制请求参数
    @ResponseBody
    @RequestMapping(value = "/test2", params = {"age=18", "username", "gender!=1"})
    public String test2() {
        return "test2";
    }

    // 限制请求头
    @ResponseBody
    @RequestMapping(value = "/test3", headers = {"age=18", "username"})
    public String test3() {
        return "test3";
    }

    // 限制请求体类型
    @ResponseBody
    @RequestMapping(value = "/test4", consumes = {"application/json"})
    public String test4() {
        return "test4";
    }

    // 限制响应体类型
    @ResponseBody
    @RequestMapping(value = "/test5", produces = {"text/html"})
    public String test5() {
        return "<h1>test5</h1>";
    }
}
