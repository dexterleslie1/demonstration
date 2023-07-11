package com.future.demo.test.feign;

import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
  contextId = "testSupportDemoFeignClient",
  value = "demo-spring-boot-test",
  path = "/api/v1")
public interface TestSupportDemoFeignClient {

  @GetMapping("test1")
  ObjectResponse<String> test1() throws BusinessException;
}

