package com.future.demo.gateway.hello.world;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ApiController {

    @GetMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("成功调用/api/v1/test1接口，[param1=" + param1 + "]");
    }

    @PostMapping(value = "/api/v1/test2")
    public ResponseEntity<String> test2(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("你的请求参数param1=" + param1);
    }

    @PostMapping(value = "/api/v1/test3")
    public ResponseEntity<String> test3(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("成功调用/api/v1/test3接口，param1=" + param1);
    }

    @PostMapping(value = "/api/v1/test5")
    public ResponseEntity<String> test5(@RequestHeader(value = "header1", defaultValue = "") String header1,
                                        @RequestHeader(value = "header2", defaultValue = "") String header2) {
        return ResponseEntity.ok("成功调用/api/v1/test5接口，header1=" + header1 + ",header2=" + header2);
    }

    @PostMapping(value = "/api/v1/test6")
    public ResponseEntity<String> test6(@RequestHeader(value = "header1", defaultValue = "") String header1,
                                        @RequestParam(value = "userId", defaultValue = "0") Long userId) {
        return ResponseEntity.ok("成功调用/api/v1/test6接口，header1=" + header1 + ",userId=" + userId);
    }

    @GetMapping("/api/v1/oss/getObject")
    public ResponseEntity<String> getObject(
            @RequestHeader(value = "header1", defaultValue = "") String header1,
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "bucketName", defaultValue = "") String bucketName,
            @RequestHeader(value = "objectName", defaultValue = "") String objectName) {
        try {
            objectName = URLDecoder.decode(objectName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        if(StringUtils.isBlank(bucketName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("没有指定bucketName");
        }
        if(StringUtils.isBlank(objectName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("没有指定objectName");
        }
        return ResponseEntity.ok().body("param1=" + param1 + ",header1=" + header1 + ",bucketName=" + bucketName + ",objectName=" + objectName);
    }

    @PostMapping("/api/v1/testResponseWithHttp400")
    public ResponseEntity<String> testResponseWithHttp400() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("预期中异常");
    }
}
