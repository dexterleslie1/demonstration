package com.future.demo.test.feign;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.demo.PetModel;
import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        contextId = "testSupportDemoFeignClient",
        value = "demo-spring-boot-test",
        path = "/api/v1/pet")
public interface TestSupportDemoFeignClient {

    @PostMapping("add")
    ObjectResponse<Long> add(@RequestParam(value = "name", defaultValue = "") String name,
                               @RequestParam(value = "age", defaultValue = "0") Integer age);

    @DeleteMapping("delete")
    ObjectResponse<String> delete(@RequestParam(value = "id", defaultValue = "0") Long id);

    @PutMapping("update")
    ObjectResponse<String> update(@RequestParam(value = "id", defaultValue = "0") Long id,
                                  @RequestParam(value = "name", defaultValue = "") String name,
                                  @RequestParam(value = "age", defaultValue = "0") Integer age);

    @GetMapping("get")
    ObjectResponse<PetModel> get(@RequestParam(value = "id", defaultValue = "0") Long id);
}

