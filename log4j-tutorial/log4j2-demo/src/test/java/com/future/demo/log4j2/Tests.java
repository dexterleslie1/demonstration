package com.future.demo.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Tests {
    final static Logger log = LogManager.getLogger(Tests.class);

    @Test
    public void test() {
        String username = "${java:os}";
        log.debug("username={}", username);
    }
}
