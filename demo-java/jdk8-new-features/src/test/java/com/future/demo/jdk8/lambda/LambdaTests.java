package com.future.demo.jdk8.lambda;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
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

    @Test
    public void testLambdaWithoutArgs() {
        String str = "echo:";
        LambdaWithoutArgsInterface withoutArgsInterface = () -> str;
        Assert.assertEquals(str, withoutArgsInterface.echo());

        Assert.assertEquals(str, ((LambdaWithoutArgsInterface)()->str).echo());
    }

    @Test
    public void testList() {
        // 使用lambda遍历集合
        List<String> stringList = new ArrayList<>();
        stringList.add("01");
        stringList.add("02");
        stringList.add("03");

        List<String> stringList2 = new ArrayList<>();
        stringList.forEach(str->stringList2.add(str));
        Assert.assertArrayEquals(stringList.toArray(), stringList2.toArray());

        // 使用lambda集合排序
        List<ListEntry> listEntryList = new ArrayList<>();
        listEntryList.add(new ListEntry(2));
        listEntryList.add(new ListEntry(3));
        listEntryList.add(new ListEntry(9));
        listEntryList.add(new ListEntry(7));
        listEntryList.sort((o1, o2) -> o1.getNumber()-o2.getNumber());
        Assert.assertEquals(2, listEntryList.get(0).getNumber());
        Assert.assertEquals(3, listEntryList.get(1).getNumber());
        Assert.assertEquals(7, listEntryList.get(2).getNumber());
        Assert.assertEquals(9, listEntryList.get(3).getNumber());
    }
}
