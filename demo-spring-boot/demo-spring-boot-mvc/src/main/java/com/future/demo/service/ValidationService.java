package com.future.demo.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
// 注意：方法参数校验必须在类中添加 @Validated 注解，否则校验注解不生效
@Validated
public class ValidationService {
    public void testSingleParam(
            @NotBlank(message = "{param.required.p1}") String p1) {
    }

    /**
     * 协助测试ConstraintViolationException两个参数异常情况处理
     *
     * @param p1
     * @param p2
     */
    public void testDoubleParam(
            @NotBlank(message = "没有提供参数1") String p1,
            @NotBlank(message = "没有提供参数2") String p2) {

    }
}
