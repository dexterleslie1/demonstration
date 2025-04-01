package com.future.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @RequestMapping(value = "/login-success", produces = "text/plain;charset=UTF-8")
    public String loginSuccess() {
        return "登录成功";
    }

    @RequestMapping(value = "/r/r1", produces = "text/plain;charset=UTF-8")
    public String r1() {
        return "资源 r1 访问成功";
    }

    @RequestMapping(value = "/r/r2", produces = "text/plain;charset=UTF-8")
    public String r2() {
        return "资源 r2 访问成功";
    }
}
