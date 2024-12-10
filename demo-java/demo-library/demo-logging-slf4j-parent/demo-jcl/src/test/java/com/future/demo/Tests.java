package com.future.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class Tests {
    @Test
    public void test1() {
        Log log = LogFactory.getLog(Tests.class);

        log.debug("Hello World!");
        log.info("Hello World!");
    }
}
