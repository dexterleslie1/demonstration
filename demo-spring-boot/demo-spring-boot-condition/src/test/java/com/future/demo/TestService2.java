package com.future.demo;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dexterleslie@gmail.com
 */
public class TestService2 {
    @Autowired
    TestService1 testService1 = null;
}
