package com.future.demo.common.feign;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        contextId = "bpkcMxbFeign",
        value = "demo-spring-cloud-low-memory-provider",
        path = "/api/v1/bpkc-mxb",
        configuration = BpkcMxbFeignConfig.class)
public interface BpkcMxbFeign {
    @GetMapping("random")
    Response random(@RequestParam(value = "limit", defaultValue = "20000") Integer limit);
}
