package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.properties.TestProperties;
import com.future.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/")
    public ObjectResponse<TestProperties> index() {
        ObjectResponse<TestProperties> response = new ObjectResponse<>();
        response.setData(testService.getTestProperties());
        return response;
    }
}
