package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTests {

    @Test
    public void test() {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        List<Long> subList = list.subList(0, 1);
        Assert.assertEquals(1, subList.size());
        Assert.assertEquals(Long.valueOf(1L), subList.get(0));

        subList = list.subList(0, 3);
        Assert.assertEquals(3, subList.size());
    }
}
