package com.future.demo;

import org.apache.log4j.Logger;
import org.junit.Test;

public class Tests {

    @Test
    public void test1() {
        Logger logger = Logger.getLogger(Tests.class);

        logger.debug("Hello World");
        logger.info("Hello World");
    }
}
