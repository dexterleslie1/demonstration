package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject("Hello world!");
    }
}
