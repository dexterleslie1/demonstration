package com.future.demo.java.commons.lang;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

public class RandomUtilsTests {
    @Test
    public void test() {
        int startInclusive = 1;
        int endExclusive = Integer.MAX_VALUE;
        for(int i=0; i<100000000; i++) {
            int randomInt = RandomUtils.nextInt(startInclusive, endExclusive);
            Assert.assertTrue(randomInt>=startInclusive && randomInt<endExclusive);
        }
    }
}
