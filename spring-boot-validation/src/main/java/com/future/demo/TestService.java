package com.future.demo;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
// 启用springboot validation
@Validated
public class TestService {
    public void testSingleParam(@NotNull(message = "{param.required.p1}")
                                @NotBlank(message = "{param.required.p1}") String p1) {
    }
}
