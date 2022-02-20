package com.future.demo.jdk8.optional;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class OptionalTests {
    @Test
    public void test() {
        // Optional.of(null)会抛出NullPointerException
        try {
            Optional.of(null);
            Assert.fail("预期异常没有抛出");
        } catch (NullPointerException ex) {
        }

        // Optional.ofNullable(null).get()会抛出异常
        try {
            Optional.ofNullable(null).get();
            Assert.fail("预期异常没有抛出");
        } catch (NoSuchElementException ex) {

        }

        // Optional.ofNullable(null)不会抛出异常
        Optional.ofNullable(null);

        // isPresent用法
        Assert.assertFalse(Optional.ofNullable(null).isPresent());
        Assert.assertTrue(Optional.ofNullable("").isPresent());
        Assert.assertTrue(Optional.of(new ArrayList<>()).isPresent());

        // orElse返回默认值
        Assert.assertEquals("default", Optional.ofNullable(null).orElse("default"));
        Assert.assertEquals("myValue", Optional.ofNullable("myValue").orElse("default"));

        // orElseGet用法
        AtomicInteger atomicInteger3 = new AtomicInteger();
        Assert.assertEquals("default", Optional.ofNullable(null).orElseGet(() -> {
            atomicInteger3.incrementAndGet();
            return "default";
        }));
        Assert.assertEquals(1, atomicInteger3.get());

        // filter用法
        Assert.assertFalse(Optional.ofNullable(null).filter(value -> "Dexter".equals(value)).isPresent());
        Assert.assertFalse(Optional.ofNullable("dexter").filter(value -> "Dexter".equals(value)).isPresent());
        Assert.assertTrue(Optional.ofNullable("Dexter").filter(value -> "Dexter".equals(value)).isPresent());

        // ifPresent用法
        AtomicInteger atomicInteger = new AtomicInteger();
        Optional.ofNullable(null).ifPresent(value -> atomicInteger.incrementAndGet());
        Assert.assertEquals(0, atomicInteger.get());

        AtomicInteger atomicInteger1 = new AtomicInteger();
        Optional.ofNullable("Dexter").ifPresent(value -> atomicInteger1.incrementAndGet());
        Assert.assertEquals(1, atomicInteger1.get());

        // 综合案例
        InternalOrder order = null;
        String ordername = Optional.ofNullable(order).map(orderT -> orderT.getName()).map(ordernameT -> ordernameT.toLowerCase()).orElse("default");
        Assert.assertEquals("default", ordername);
        ordername = Optional.ofNullable(new InternalOrder("Dexter")).map(orderT -> orderT.getName()).map(ordernameT -> ordernameT.toLowerCase()).orElse("default");
        Assert.assertEquals("dexter", ordername);
    }

    static class InternalOrder {
        private String name;

        public InternalOrder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
