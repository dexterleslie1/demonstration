package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("testBusinessException")
    ObjectResponse<String> testBusinessException() throws BusinessException {
        throw new BusinessException("测试BusinessException");
    }

    @GetMapping("testIllegalArgumentException")
    ObjectResponse<String> testIllegalArgumentException() {
        throw new IllegalArgumentException("测试IllegalArgumentException");
    }
}
