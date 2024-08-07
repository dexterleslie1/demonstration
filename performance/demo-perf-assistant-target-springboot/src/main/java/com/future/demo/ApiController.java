package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ApiController {

    @GetMapping
    ObjectResponse<String> test() {
        String uuidStr = UUID.randomUUID().toString();
        return ResponseUtils.successObject("uuidStr: " + uuidStr);
    }
}
