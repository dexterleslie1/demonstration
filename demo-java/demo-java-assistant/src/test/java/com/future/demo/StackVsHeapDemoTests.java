package com.future.demo;

import org.junit.Test;

/**
 * 演示 Java 中变量在栈与堆中的存放位置。
 * <ul>
 *   <li>栈：方法栈帧中的局部变量（基本类型值、对象引用）、方法参数</li>
 *   <li>堆：new 出来的对象、数组、对象的实例字段</li>
 * </ul>
 */
public class StackVsHeapDemoTests {

    /** 实例字段 → 随对象存在堆中 */
    private int instanceField = 1;

    @Test
    public void demoStackVsHeap() {
        // ---------- 在栈中（当前方法的栈帧）----------
        // 局部基本类型 int，值在栈
        int localPrimitive = 42;
        // 局部引用变量，引用本身在栈（它们指向的对象在堆）
        String localRef = "hello";
        // 局部引用变量，引用本身在栈（它们指向的对象在堆）
        // 通过 new 创建的对象在堆
        Object localObjRef = new Object();

        // ---------- 在堆中 ----------
        // localObjRef 指向的对象在堆
        // 通过 new 创建的对象/数组在堆
        int[] arrayOnHeap = new int[]{1, 2, 3};
        // 通过引用访问的实例字段所在的对象在堆
        int x = this.instanceField;

        // 避免“未使用”告警，仅作演示
        use(localPrimitive, localRef, localObjRef, arrayOnHeap, x);
    }

    private void use(Object... args) {
    }
}
