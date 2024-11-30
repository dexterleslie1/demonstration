package com.future.demo.controller;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// 测试响应体
@RestController
@RequestMapping("/api/v1/response")
public class ResponseController {

    // 测试JSON响应体
    @RequestMapping("/test1")
    public Person test1() {
        Person person = new Person();
        person.setName("张三");
        person.setAge(20);
        person.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        person.setAddress(new Person.Address("北京市", "海淀区"));
        return person;
    }

    // 测试文件下载响应体
    // HttpEntity代表整个请求体，其中包含了请求头和请求体
    // ResponseEntity代表整个响应体，其中包含了响应头和响应体
    @RequestMapping("/test2")
    public ResponseEntity<InputStreamResource> test2() throws IOException {
        ClassPathResource resource = new ClassPathResource("test.txt");
        InputStream inputStream = resource.getInputStream();

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        String filename = URLEncoder.encode("测试文件.txt", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(inputStream.available())
                .header("Content-Disposition", "attachment;filename=" + filename)
                .body(inputStreamResource);
    }
}
