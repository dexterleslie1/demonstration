package com.future.demo.jdk8.interfaceu;

import org.junit.Assert;
import org.junit.Test;

public class Jdk8InterfaceImplTests {
    @Test
    public void test() {
        Jdk8Interface jdk8Interface = new Jdk8InterfaceImpl();

        int a = 1;
        int b = 2;
        int intReturn = jdk8Interface.add(a, b);
        Assert.assertEquals(a + b, intReturn);

        String str = "8888";
        String strReturn = jdk8Interface.defaultMethod(str);
        Assert.assertEquals(String.format("echo:%s", str), strReturn);

        strReturn = Jdk8Interface.staticMethod(str);
        Assert.assertEquals(String.format("echo:%s", str), strReturn);
    }
}
