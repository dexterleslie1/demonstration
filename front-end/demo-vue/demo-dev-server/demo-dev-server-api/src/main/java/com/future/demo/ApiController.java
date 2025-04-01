package com.future.demo;

import com.future.common.http.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated
public class ApiController {
    @GetMapping("get")
    ObjectResponse<String> get() {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("Hi there, your uuid is " + UUID.randomUUID());
        return response;
    }
}
