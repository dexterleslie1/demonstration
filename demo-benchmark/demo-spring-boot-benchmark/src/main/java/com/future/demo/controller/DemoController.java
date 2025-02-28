package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class DemoController {

    @RequestMapping(value = "/")
    public ObjectResponse<String> index() {
        String str = "UUID:" + UUID.randomUUID();
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(str);
        return response;
    }
}
