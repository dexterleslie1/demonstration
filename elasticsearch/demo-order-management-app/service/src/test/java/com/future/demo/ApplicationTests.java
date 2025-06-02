package com.future.demo;

import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class ApplicationTests {

//    @Resource
//    ProductMapper productMapper;
//    @Resource
//    OrderMapper orderMapper;
//    @Resource
//    OrderDetailMapper orderDetailMapper;

    @Resource
    OrderService orderService;

    @Test
    public void contextLoads() throws Exception {

    }
}
