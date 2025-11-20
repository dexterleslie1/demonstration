package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomTests {
    @Test
    public void test() {
        int minimum = 0;
        int maximum = 10;
        List<Integer> allList = new ArrayList<>();
        for(int i=minimum; i<maximum; i++) {
            allList.add(i);
        }

        List<Integer> randomGenList = new ArrayList<>();
        for(int i=0; i<1000; i++) {
            int randomInt = ThreadLocalRandom.current().nextInt(minimum, maximum);
            if(!randomGenList.contains(randomInt)) {
                randomGenList.add(randomInt);
            }
        }

        Collections.sort(allList);
        Collections.sort(randomGenList);
        Assert.assertArrayEquals(randomGenList.toArray(), allList.toArray());
    }
}
