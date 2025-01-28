package com.future.demo.jdk8.method.references;

import com.future.demo.jdk8.interfaceu.Jdk8Interface;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 方法引用测试
 * https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
 */
@Slf4j
public class MethodReferencesTests {
    // 测试为何JDK8引入方法引用特性
    @Test
    public void testWhyIntroduceMethodReferencesFeature() {
        // 不使用方法引用的函数式接口，代码冗长
        Supplier<Integer> supplierMaximumInteger1 = () -> {
            Integer[] integerArr = new Integer[]{23, 2, 54, 19};
            Integer maximumValue = null;
            for (Integer i : integerArr) {
                if (maximumValue == null) {
                    maximumValue = i;
                }

                if (i > maximumValue) {
                    maximumValue = i;
                }
            }

            return maximumValue;
        };
        Assert.assertEquals(Integer.valueOf(54), supplierMaximumInteger1.get());

        // 使用方法引用的函数式接口，代码简洁
        Supplier<Integer> supplierMaximumInteger2 = MethodReferencesTests::getMaximumInteger;
        Assert.assertEquals(Integer.valueOf(54), supplierMaximumInteger2.get());
    }

    static Integer getMaximumInteger() {
        Integer[] integerArr = new Integer[]{23, 2, 54, 19};
        Integer maximumValue = null;
        for (Integer i : integerArr) {
            if (maximumValue == null) {
                maximumValue = i;
            }

            if (i > maximumValue) {
                maximumValue = i;
            }
        }

        return maximumValue;
    }

    /**
     * 静态方法引用
     */
    @Test
    public void test_static_method_reference() {
        Jdk8Interface jdk8Interface = MethodReferencesTests::testAdd;
        int result = jdk8Interface.add(1, 2);
        Assert.assertEquals(3, result);

        Supplier<Long> supplier = System::currentTimeMillis;
        Long milliseconds = supplier.get();
        log.debug("milliseconds=" + milliseconds);
    }

    static int testAdd(int a, int b) {
        return a + b;
    }

    /**
     * 实例方法引用
     */
    @Test
    public void test_instance_method_reference() {
        TestClassInstanceMethodReference instance = new TestClassInstanceMethodReference(5);
        Jdk8Interface jdk8Interface = instance::testAdd;
        int result = jdk8Interface.add(1, 2);
        Assert.assertEquals(3 + instance.getAdditional(), result);

        Date now = new Date();
        Supplier<Long> supplier = now::getTime;
        Long milliseconds = supplier.get();
        log.debug("milliseconds=" + milliseconds);
    }

    static class TestClassInstanceMethodReference {
        private final int additional;

        public TestClassInstanceMethodReference(int additional) {
            this.additional = additional;
        }

        public int getAdditional() {
            return this.additional;
        }

        int testAdd(int a, int b) {
            return a + b + additional;
        }
    }

    /**
     * 构造方法引用
     */
    @Test
    public void test_constructor_method_reference() {
        ConstructorMethodReferenceInterface referenceInterface = ConstructorMethodReferenceEntity::new;
        Assert.assertEquals("测试", referenceInterface.get("测试").getStr());
    }

    /**
     * 类名引用实例方法
     * 注意：实际上是将方法调用第一个参数作为实例引用方法的调用者，例如：将 function1.apply("Hello") 方法调用第一个参数 Hello 作为 String 实例引用方法的调用者 "Hello".length()
     */
    @Test
    public void test_instance_method_reference_of_particular_type() {
        InstanceMethodReferenceOfParticularTypeInterface referenceInterface = InstanceMethodReferenceOfParticularTypeClass::getStr;
        // 相当于调用 new InstanceMethodReferenceOfParticularTypeClass("测试") 实例的 getStr 方法
        Assert.assertEquals("测试", referenceInterface.get(new InstanceMethodReferenceOfParticularTypeClass("测试")));

        Function<String, Integer> function1 = String::length;
        // 相当于调用 "Hello".length() 方法
        Integer length = function1.apply("Hello");
        Assert.assertEquals(Integer.valueOf(5), length);

        BiFunction<String, Integer, String> biFunction = String::substring;
        // 相当于调用 "Hello World!".substring(3) 方法
        String subStr = biFunction.apply("Hello World!", 3);
        Assert.assertEquals("lo World!", subStr);
    }

    // 数组构造器方法引用
    @Test
    public void testArrayConstructorMethodReference() {
        Function<Integer, String[]> function = String[]::new;
        String[] strArr = function.apply(10);
        Assert.assertEquals(10, strArr.length);
    }
}
