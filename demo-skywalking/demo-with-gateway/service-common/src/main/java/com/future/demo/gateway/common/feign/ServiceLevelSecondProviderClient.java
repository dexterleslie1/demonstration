package com.future.demo.gateway.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-level-second-provider")
public interface ServiceLevelSecondProviderClient {

    @GetMapping(value = "/api/v1/test1")
    ResponseEntity<String> test1(@RequestParam(value = "param1") String param1);

    @GetMapping(value = "/api/v1/test2")
    ResponseEntity<String> test2(@RequestParam(value = "param1") String param1);

    @GetMapping(value = "/api/v1/test3")
    ResponseEntity<String> test3();

}
