package com.future.demo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tests {
    @Test
    public void test1() {
        Logger logger = LoggerFactory.getLogger(Tests.class);

        logger.debug("Hello world!");
        logger.info("Hello world!");
    }
}
