package com.future.demo.zuul.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Service;

@Service
public class CommonService {
    @SentinelResource(value="common1")
    public void test1() {

    }
}
