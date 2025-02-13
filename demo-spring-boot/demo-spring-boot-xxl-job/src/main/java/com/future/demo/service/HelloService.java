package com.future.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelloService {
    public void methodA() {
        log.debug("执行MethodA的方法");
    }

    public void methodB() {
        log.debug("执行MethodB的方法");
    }
}
