package com.future.demo.spring.cloud.hystrix.controller;

import com.future.demo.spring.cloud.common.ObjectResponse;
import com.future.demo.spring.cloud.hystrix.feign.ApiUser;
import com.future.demo.spring.cloud.hystrix.client.ApiUserRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ZuulControllerUser {
    @Autowired
    ApiUser apiUser;
    @Autowired
    ApiUserRestTemplate apiUserRestTemplate;

    @PostMapping("timeoutWithFeignFallback")
    ResponseEntity<ObjectResponse<String>> timeoutWithFeignFallback(@RequestParam(value = "milliseconds") Integer milliseconds) {
        return this.apiUser.timeout(milliseconds);
    }

    @PostMapping("timeoutWithFeignFallback2")
    ResponseEntity<ObjectResponse<String>> timeoutWithFeignFallback2(@RequestParam(value = "milliseconds") Integer milliseconds) {
        return this.apiUser.timeout2(milliseconds);
    }

    @PostMapping("timeoutWithRestTemplate")
    ResponseEntity<ObjectResponse<String>> timeoutWithRestTemplate(@RequestParam(value = "milliseconds") Integer milliseconds) {
        return this.apiUserRestTemplate.timeout(milliseconds);
    }

    @PostMapping("timeoutWithRestTemplate2")
    ResponseEntity<ObjectResponse<String>> timeoutWithRestTemplate2(@RequestParam(value = "milliseconds") Integer milliseconds) {
        return this.apiUserRestTemplate.timeout2(milliseconds);
    }
}
