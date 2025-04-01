package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("test1")
    ResponseEntity<ObjectResponse<String>> test1(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false, defaultValue = "") String token) {
        return ResponseEntity.ok()
                .header("x-my-request-id", UUID.randomUUID().toString())
                .body(ResponseUtils.successObject("Nuxt api返回数据, token=" + token));
    }
}
