package com.future.demo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class Tests {
    @Test
    public void test1() {
        Logger logger = LoggerFactory.getLogger(Tests.class);

        logger.debug("Hello World");
        logger.info("Hello World");
    }
}
