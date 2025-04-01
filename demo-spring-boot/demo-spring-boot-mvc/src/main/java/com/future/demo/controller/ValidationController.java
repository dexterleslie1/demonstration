package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
// 注意：方法参数校验必须在类中添加 @Validated 注解，否则校验注解不生效
@Validated
public class ValidationController {
    @GetMapping(value = "testSingleParam")
    public ObjectResponse<String> testSingleParam(
            @NotNull(message = "{param.required.p1}")
            @NotBlank(message = "{param.required.p1}")
            @RequestParam(value = "p1", defaultValue = "") String p1) {
        return ResponseUtils.successObject("成功调用");
    }
}
