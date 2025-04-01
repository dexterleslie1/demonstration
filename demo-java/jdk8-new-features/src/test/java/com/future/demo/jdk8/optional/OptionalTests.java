package com.future.demo.jdk8.optional;

import com.future.demo.jdk8.stream.UserEntity;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OptionalTests {
    @Test
    public void test() {
        // 以前对 null 的处理方式，需要使用 if else 判断变量是否为 null 导致代码不优雅
        String username = "Dexter";
        if (username != null) {
            Assert.assertNotNull(username);
        } else {
            Assert.assertNull(username);
        }

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

        // Optional.empty() 创建空 Optional 对象
        String str = (String) Optional.empty().orElse("Hello world!");
        Assert.assertEquals("Hello world!", str);

        // isPresent用法
        Assert.assertFalse(Optional.ofNullable(null).isPresent());
        Assert.assertTrue(Optional.ofNullable("").isPresent());
        Assert.assertTrue(Optional.of(new ArrayList<>()).isPresent());

        // 如果为空则 get 方法抛出 NoSuchElementException
        try {
            Optional.ofNullable(null).get();
            Assert.fail();
        } catch (NoSuchElementException ignored) {
        }

        // 如果为空则orElse返回默认值
        Assert.assertEquals("default", Optional.ofNullable(null).orElse("default"));
        Assert.assertEquals("myValue", Optional.ofNullable("myValue").orElse("default"));

        // 如果为空则调用orElseGet提供的 Supplier 获取值
        AtomicInteger atomicInteger3 = new AtomicInteger();
        Assert.assertEquals("default", Optional.ofNullable(null).orElseGet(() -> {
            atomicInteger3.incrementAndGet();
            return "default";
        }));
        Assert.assertEquals(1, atomicInteger3.get());

        // 如果值存在则调用 ifPresent 提供的 Consumer，否则不调用
        AtomicInteger atomicInteger = new AtomicInteger();
        Optional.ofNullable(null).ifPresent(value -> atomicInteger.incrementAndGet());
        Assert.assertEquals(0, atomicInteger.get());

        AtomicInteger atomicInteger1 = new AtomicInteger();
        Optional.ofNullable("Dexter").ifPresent(value -> atomicInteger1.incrementAndGet());
        Assert.assertEquals(1, atomicInteger1.get());

        // filter 用法
        Assert.assertFalse(Optional.ofNullable(null).filter(value -> "Dexter".equals(value)).isPresent());
        Assert.assertFalse(Optional.ofNullable("dexter").filter(value -> "Dexter".equals(value)).isPresent());
        Assert.assertTrue(Optional.ofNullable("Dexter").filter(value -> "Dexter".equals(value)).isPresent());

        // map 用法
        InternalOrder order = null;
        String ordername = Optional.ofNullable(order).map(orderT -> orderT.getName()).map(ordernameT -> ordernameT.toLowerCase()).orElse("default");
        Assert.assertEquals("default", ordername);
        ordername = Optional.ofNullable(new InternalOrder("Dexter")).map(orderT -> orderT.getName()).map(ordernameT -> ordernameT.toLowerCase()).orElse("default");
        Assert.assertEquals("dexter", ordername);

        // 操作List
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity("zhangsan", 20));
        userEntityList.add(new UserEntity("lisi", 13));
        List<String> nameList =
                Optional.ofNullable(userEntityList).orElseGet(Collections::emptyList).stream().map(o -> o.getName())
                        .collect(Collectors.toList());
        Assert.assertArrayEquals(Arrays.asList("zhangsan", "lisi").toArray(new String[]{}), nameList.toArray(new String[]{}));
        userEntityList = null;
        nameList =
                Optional.ofNullable(userEntityList).orElseGet(Collections::emptyList).stream().map(o -> o.getName())
                        .collect(Collectors.toList());
        Assert.assertEquals(0, nameList.size());
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
