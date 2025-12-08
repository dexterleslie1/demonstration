package com.future.demo;

import com.future.demo.service.DeptService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiApplicationTests {

    @Resource
    DeptService deptService;

    @Test
    void contextLoads() {
        deptService.list(null);
    }

}
