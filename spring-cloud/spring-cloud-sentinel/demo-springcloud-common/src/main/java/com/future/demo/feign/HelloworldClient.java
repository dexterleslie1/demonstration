package com.future.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "demo-springcloud-helloworld")
public interface HelloworldClient {
    @GetMapping(value = "/api/v1/test1")
    ResponseEntity<String> test1(@RequestParam(value = "param1") String param1);
}
