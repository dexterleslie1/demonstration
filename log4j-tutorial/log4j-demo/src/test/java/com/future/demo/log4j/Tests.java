package com.future.demo.log4j;

import org.apache.log4j.Logger;
import org.junit.Test;

public class Tests {
    final static Logger log = Logger.getLogger(Tests.class);

    @Test
    public void test() {
        String username = "${java:os}";
        log.debug("username=" + username);
    }
}
