package com.future.demo.logback;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Tests {
    @Test
    public void test() {
        log.debug("这是debug消息");
        log.error("这是error消息");
    }
}
