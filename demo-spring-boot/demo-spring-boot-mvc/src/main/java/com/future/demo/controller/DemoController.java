package com.future.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// ?：代表匹配任意一个字符
// *：代表匹配任意多个字符
// **：代表匹配任意多层路径
@Controller
public class DemoController {
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
