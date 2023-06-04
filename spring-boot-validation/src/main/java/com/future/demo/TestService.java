package com.future.demo;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
// 启用springboot validation
@Validated
public class TestService {
    public void testSingleParam(@NotNull(message = "没有提供p1参数")
                                @NotBlank(message = "没有提供p1参数") String p1) {
    }
}
