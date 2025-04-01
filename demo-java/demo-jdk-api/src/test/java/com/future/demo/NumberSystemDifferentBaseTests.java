package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

/**
 * 不同进制数转换
 */
public class NumberSystemDifferentBaseTests {
    @Test
    public void test() {
        //#region 从字符串按照指定进制转换为Integer

        // 使用2进制从字符串转换Integer
        String str = "0";
        Integer intValue = Integer.parseInt(str, 2);
        Assert.assertEquals("0", String.valueOf(intValue));
        str = "01";
        intValue = Integer.parseInt(str, 2);
        Assert.assertEquals("1", String.valueOf(intValue));
        str = "10";
        intValue = Integer.parseInt(str, 2);
        Assert.assertEquals("2", String.valueOf(intValue));
        str = "11";
        intValue = Integer.parseInt(str, 2);
        Assert.assertEquals("3", String.valueOf(intValue));
        str = "100";
        intValue = Integer.parseInt(str, 2);
        Assert.assertEquals("4", String.valueOf(intValue));

        // 使用8进制从字符串转换Integer
        str = "7";
        intValue = Integer.parseInt(str, 8);
        Assert.assertEquals("7", String.valueOf(intValue));
        str = "10";
        intValue = Integer.parseInt(str, 8);
        Assert.assertEquals("8", String.valueOf(intValue));
        str = "11";
        intValue = Integer.parseInt(str, 8);
        Assert.assertEquals("9", String.valueOf(intValue));
        str = "20";
        intValue = Integer.parseInt(str, 8);
        Assert.assertEquals("16", String.valueOf(intValue));

        // 使用10进制从字符串转换Integer
        str = "9";
        intValue = Integer.parseInt(str, 10);
        Assert.assertEquals("9", String.valueOf(intValue));

        // 使用16进制从字符串转换Integer
        str = "f";
        intValue = Integer.parseInt(str, 16);
        Assert.assertEquals("15", String.valueOf(intValue));
        str = "10";
        intValue = Integer.parseInt(str, 16);
        Assert.assertEquals("16", String.valueOf(intValue));
        str = "11";
        intValue = Integer.parseInt(str, 16);
        Assert.assertEquals("17", String.valueOf(intValue));

        //#endregion


        //#region 从Integer转换为指定进制数字符串

        // 转换为2进制字符串
        intValue = 5;
        str = Integer.toBinaryString(intValue);
        Assert.assertEquals("101", str);

        // 转换为10进制字符串
        intValue = 5;
        str = Integer.toString(intValue);
        Assert.assertEquals("5", str);

        // 转换为8进制字符串
        intValue = 8;
        str = Integer.toOctalString(intValue);
        Assert.assertEquals("10", str);

        // 转换为16进制字符串
        intValue = 16;
        str = Integer.toHexString(intValue);
        Assert.assertEquals("10", str);

        //#endregion


        //#region 使用指定进制数定义变量

        // 使用2进制定义变量
        // https://www.baeldung.com/java-binary-numbers
        // https://stackoverflow.com/questions/867365/in-java-can-i-define-an-integer-constant-in-binary-format
        intValue = 0b101;
        Assert.assertEquals(Integer.valueOf(5), intValue);

        // 使用8进制定义变量
        // https://stackoverflow.com/questions/16433781/how-to-set-value-of-octal-in-java
        intValue = 010;
        Assert.assertEquals(Integer.valueOf(8), intValue);

        // 使用16进制定义变量
        intValue = 0x10;
        Assert.assertEquals(Integer.valueOf(16), intValue);

        //#endregion

        //#region 使用指定进制显示显示byte

        // 使用2进制显示byte
        // https://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
        byte byteValue = 16;
        str = String.format("%8s", Integer.toBinaryString(byteValue & 0XFF)).replaceAll(" ", "0");
        Assert.assertEquals("00010000", str);

        // 使用16进制显示byte
        byteValue = 8;
        str = String.format("%2s", Integer.toHexString(byteValue & 0XFF)).replaceAll(" ", "0");
        Assert.assertEquals("08", str);
        byteValue = (byte)255;
        str = String.format("%2s", Integer.toHexString(byteValue & 0XFF)).replaceAll(" ", "0");
        Assert.assertEquals("ff", str);

        //#endregion
    }
}
