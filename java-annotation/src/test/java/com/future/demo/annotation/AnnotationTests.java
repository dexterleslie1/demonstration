package com.future.demo.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class AnnotationTests {
    @Test
    public void test() throws NoSuchFieldException {
        // 测试AnnotationRetentionSource
        Assert.assertFalse(TestClass.class.isAnnotationPresent(AnnotationRetentionSource.class));

        // 测试AnnotationRetentionClass
        Assert.assertFalse(TestClass.class.isAnnotationPresent(AnnotationRetentionClass.class));

        // 测试Annotation1
        Assert.assertTrue(TestClass.class.isAnnotationPresent(Annotation1.class));
        Annotation1 annotation1 = TestClass.class.getAnnotation(Annotation1.class);
        Assert.assertEquals(11, annotation1.value());

        // 测试Annotation2
        Field field=TestClass.class.getDeclaredField("field1");
        Assert.assertTrue(field.isAnnotationPresent(Annotation2.class));
        Annotation2 annotation2=field.getAnnotation(Annotation2.class);
        Assert.assertEquals(12, annotation2.value());
    }
}
