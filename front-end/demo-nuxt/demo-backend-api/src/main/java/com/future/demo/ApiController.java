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
    ResponseEntity<ObjectResponse<String>> test1(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false, defaultValue = "") String token,
            @RequestParam(value = "exceptionType", required = false) String exceptionType
    ) {
        if (exceptionType != null && exceptionType.equals("bizExceptionWithHttp200")) {
            // 业务异常，http 200
            return ResponseEntity.ok()
                    .header("x-my-request-id", UUID.randomUUID().toString())
                    .body(ResponseUtils.failObject(90000, "模拟业务异常"));
        } else if (exceptionType != null && exceptionType.equals("bizExceptionWithHttp400")) {
            // 业务异常，http 400
            return ResponseEntity.badRequest()
                    .header("x-my-request-id", UUID.randomUUID().toString())
                    .body(ResponseUtils.failObject(90000, "模拟业务异常"));
        } else {
            return ResponseEntity.ok()
                    .header("x-my-request-id", UUID.randomUUID().toString())
                    .body(ResponseUtils.successObject("Nuxt api返回数据, token=" + token));
        }
    }
}
