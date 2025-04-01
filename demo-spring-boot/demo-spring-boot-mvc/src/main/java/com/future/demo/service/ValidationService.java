package com.future.demo.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
// 注意：方法参数校验必须在类中添加 @Validated 注解，否则校验注解不生效
@Validated
public class ValidationService {
    public void testSingleParam(
            @NotNull(message = "{param.required.p1}")
            @NotBlank(message = "{param.required.p1}") String p1) {
    }
}
