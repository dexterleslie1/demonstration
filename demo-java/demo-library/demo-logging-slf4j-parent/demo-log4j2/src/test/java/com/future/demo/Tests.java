package com.future.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Tests {
    @Test
    public void test1() {
        Logger logger = LogManager.getLogger(Tests.class);

        logger.debug("Hello World!");
        logger.info("Hello World!");
    }
}
