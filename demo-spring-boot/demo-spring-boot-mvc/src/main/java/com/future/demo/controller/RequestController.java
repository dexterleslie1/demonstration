package com.future.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// 测试请求参数
@RestController
@RequestMapping("/api/v1")
public class RequestController {
    // 测试@RequestParam
    @RequestMapping("test1")
    public String test1(@RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "age", defaultValue = "15") Integer age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试pojo
    @RequestMapping("test2")
    public String test2(Person person) {
        return "name=" + person.getName() + ",age=" + person.getAge() + ",hobby=" + Arrays.toString(person.getHobby()) + ",address=" + person.getAddress().toString();
    }

    // 测试@RequestHeader
    @RequestMapping("test3")
    public String test3(@RequestHeader(value = "name") String name,
                        @RequestHeader(value = "age") Integer age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试@CookieValue
    @RequestMapping("test4")
    public String test4(@CookieValue(value = "name") String name,
                        @CookieValue(value = "age", defaultValue = "15") Integer age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试@RequestBody获取JSON请求体
    @RequestMapping("test5")
    public String test5(@RequestBody Person person) {
        return "name=" + person.getName() + ",age=" + person.getAge() + ",hobby=" + Arrays.toString(person.getHobby()) + ",address=" + person.getAddress().toString();
    }

    // 测试请求路径参数
    @RequestMapping("test7/{name}/{age}")
    public String test(@PathVariable(value = "name") String name, @PathVariable("age") int age) {
        return "name=" + name + ",age=" + age;
    }

    // 测试文件上传
    @RequestMapping("test6")
    public String test6(Person person
            , @RequestParam("fileList") MultipartFile[] fileList) {
        return "name=" + person.getName() + ",age=" + person.getAge() + ",hobby="
                + Arrays.toString(person.getHobby())
                + ",address=" + person.getAddress().toString()
                + ",fileList=" + Arrays.stream(fileList).map(f -> {
            try {
                return new String(f.getBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
