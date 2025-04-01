package com.future.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// ?：代表匹配任意一个字符
// *：代表匹配任意多个字符
// **：代表匹配任意多层路径
@Controller
// controller 下所有接口都支持跨域请求，并且只允许 abc.com 域名下的前端进行访问
//@CrossOrigin(origins = "abc.com")
public class DemoController {
    // 当前接口支持跨域请求，并且只允许 abc.com 域名下的前端进行访问
    // @CrossOrigin(origins = "abc.com")
    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @ResponseBody
    @RequestMapping("/hell?")
    public String hello2() {
        return "Hell?!";
    }

    @ResponseBody
    @RequestMapping("/hell*")
    public String hello3() {
        return "Hell*!";
    }

    @ResponseBody
    @RequestMapping("/hello/**")
    public String hello4() {
        return "Hello/**!";
    }
}
