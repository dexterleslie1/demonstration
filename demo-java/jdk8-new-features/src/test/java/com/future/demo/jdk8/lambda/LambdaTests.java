package com.future.demo.jdk8.lambda;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LambdaTests {
    @Test
    public void testWhyLambda() {
        // 为何需要lambda表达式？下面演示使用匿名类实例时，代码不够精简，使用精简的lambda表达式代替

        int a = 1;
        int b = 2;
        // 使用匿名内部类语法是很冗余的
        int intReturn = new LambdaWithArgsInterface() {
            @Override
            public int add(int a, int b) {
                return a + b;
            }
        }.add(a, b);
        Assert.assertEquals(a + b, intReturn);

        // Lambda表达式的好处：可以简化匿名内部类，让代码更加精简
        intReturn = ((LambdaWithArgsInterface) (a1, b1) -> a1 + b1).add(a, b);
        Assert.assertEquals(a + b, intReturn);
    }

    // 测试无参数有返回值的 Lambda 表达式
    @Test
    public void testLambdaWithoutArgumentAndWithReturnValue() {
        String str = "echo:";
        LambdaWithoutArgsInterface withoutArgsInterface = () -> str;
        Assert.assertEquals(str, withoutArgsInterface.echo());

        Assert.assertEquals(str, ((LambdaWithoutArgsInterface) () -> str).echo());
    }

    // 测试有参数有返回值的 Lambda 表达式
    @Test
    public void testLambdaWithArgumentAndReturnValue() {
        LambdaWithArgsInterface lambdaWithArgsInterface = (a, b) -> a + b;
        Assert.assertEquals(3, lambdaWithArgsInterface.add(1, 2));
    }

    // 使用 Lambda 表达式对集合排序
    @Test
    public void testListSortedByUsingLambda() {
        // 使用lambda遍历集合
        List<String> stringList = new ArrayList<>();
        stringList.add("01");
        stringList.add("02");
        stringList.add("03");

        List<String> stringList2 = new ArrayList<>();
        stringList.forEach(str -> stringList2.add(str));
        Assert.assertArrayEquals(stringList.toArray(), stringList2.toArray());

        List<ListEntry> listEntryList = new ArrayList<>();
        listEntryList.add(new ListEntry(2));
        listEntryList.add(new ListEntry(3));
        listEntryList.add(new ListEntry(9));
        listEntryList.add(new ListEntry(7));
        //  使用 Lambda 表达式对集合排序
        listEntryList.sort((o1, o2) -> o1.getNumber() - o2.getNumber());
        Assert.assertEquals(2, listEntryList.get(0).getNumber());
        Assert.assertEquals(3, listEntryList.get(1).getNumber());
        Assert.assertEquals(7, listEntryList.get(2).getNumber());
        Assert.assertEquals(9, listEntryList.get(3).getNumber());
    }

    // 测试 Lambda 表达式省略写法
    @Test
    public void testLambdaAbbreviation() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        // 非省略写法
        list.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        // 省略写法
        // 小括号内参数的类型可以省略
        // 如果大括号内有且仅有一个语句，可以同时省略大括号、return 关键字及语句分号
        list.sort((o1, o2) -> o1 - o2);

        // 非省略写法
        list.forEach((e) -> {
            System.out.println(e);
        });
        // 省略写法
        // 如果小括号内有且仅有一个参数，则小括号可以省略
        // 如果大括号内有且仅有一个语句，可以同时省略大括号、return 关键字及语句分号
        list.forEach(e -> System.out.println(e));
    }

    // 支持使用 Lambda 表达式的条件
    @Test
    public void testSupportUsingLambdaSituation() {
        // region 方法的参数或者局部变量类型必须为接口才能使用 Lambda 表达式

        testMethod1(() -> System.out.println("Hello world!"));

        Interface1 interface1 = () -> System.out.println("Hello world2!");
        interface1.method1();

        // endregion
    }

    void testMethod1(Interface1 interface1) {
        interface1.method1();
    }

    @FunctionalInterface
    interface Interface1 {
        void method1();

        // 接口中有且仅有一个抽象方法
        // void method2();
    }
}
