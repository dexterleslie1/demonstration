package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("test1")
    ObjectResponse<String> test1(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false, defaultValue = "") String token) {
        return ResponseUtils.successObject("Nuxt api返回数据, token=" + token);
    }
}
