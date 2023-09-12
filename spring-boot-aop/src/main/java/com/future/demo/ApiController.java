package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    TestServiceI testService;

    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject(this.testService.test1(1000L, "Dexter"));
    }

}
