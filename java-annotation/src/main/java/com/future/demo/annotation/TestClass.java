package com.future.demo.annotation;

@Annotation1(value=11)
@AnnotationRetentionSource
@AnnotationRetentionClass
public class TestClass {
    @Annotation2(value=12)
    private int field1;
}
