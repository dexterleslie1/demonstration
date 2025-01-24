package com.future.demo.jdk8.method.references;

import com.future.demo.jdk8.interfaceu.Jdk8Interface;
import org.junit.Assert;
import org.junit.Test;

/**
 * 方法引用测试
 * https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
 *
 */
public class MethodReferencesTests {
    /**
     * 静态方法引用
     */
    @Test
    public void test_static_method_reference () {
        Jdk8Interface jdk8Interface = MethodReferencesTests::testAdd;
        int result = jdk8Interface.add(1, 2);
        Assert.assertEquals(3, result);
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
     * 实例方法引用指定类型
     */
    @Test
    public void test_instance_method_reference_of_particular_type() {
        InstanceMethodReferenceOfParticularTypeInterface referenceInterface = InstanceMethodReferenceOfParticularTypeClass::getStr;
        Assert.assertEquals("测试", referenceInterface.get(new InstanceMethodReferenceOfParticularTypeClass("测试")));
    }
}
