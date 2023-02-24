package com.future.demo.hystrix.feign;

import com.future.demo.spring.cloud.common.ObjectResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "spring-cloud-user", fallback = ApiUserFallback.class)
// NOTE: 千万不能再Feign interface上添加@RequestMapping，否则报告There is already 'XXXXX' bean method错误
// https://blog.csdn.net/weixin_44495198/article/details/105931661
//@RequestMapping("/api/v1/user")
public interface ApiUser {
    @GetMapping("/api/v1/user/timeout")
    ResponseEntity<ObjectResponse<String>> timeout(@RequestParam(value = "milliseconds") Integer milliseconds);

    @GetMapping("/api/v1/user/timeout2")
    ResponseEntity<ObjectResponse<String>> timeout2(@RequestParam(value = "milliseconds") Integer milliseconds);
}
