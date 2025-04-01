package com.future.demo.hystrix.controller;

import com.future.demo.hystrix.feign.ApiUser;
import com.future.demo.spring.cloud.common.ObjectResponse;
import com.future.demo.hystrix.client.ApiUserRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/user")
public class ZuulControllerUser {
    @Resource
    ApiUser apiUser;
    @Resource
    ApiUserRestTemplate apiUserRestTemplate;

    @GetMapping("timeoutWithFeignFallback")
    ResponseEntity<ObjectResponse<String>> timeoutWithFeignFallback(@RequestParam(value = "milliseconds", required = false, defaultValue = "0") Integer milliseconds) {
        return this.apiUser.timeout(milliseconds);
    }

    @GetMapping("timeoutWithFeignFallback2")
    ResponseEntity<ObjectResponse<String>> timeoutWithFeignFallback2(@RequestParam(value = "milliseconds", required = false, defaultValue = "0") Integer milliseconds) {
        return this.apiUser.timeout2(milliseconds);
    }

    @GetMapping("timeoutWithRestTemplate")
    ResponseEntity<ObjectResponse<String>> timeoutWithRestTemplate(@RequestParam(value = "milliseconds", required = false, defaultValue = "0") Integer milliseconds) {
        return this.apiUserRestTemplate.timeout(milliseconds);
    }

    @GetMapping("timeoutWithRestTemplate2")
    ResponseEntity<ObjectResponse<String>> timeoutWithRestTemplate2(@RequestParam(value = "milliseconds", required = false, defaultValue = "0") Integer milliseconds) {
        return this.apiUserRestTemplate.timeout2(milliseconds);
    }
}
