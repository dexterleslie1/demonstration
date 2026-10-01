package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class BigIntegerAndBigDecimalTests {
    @Test
    public void test() {
        // region 从二进制字符串构造BigInteger和BigDecimal

        String binaryString = "10101010101010101010101010101010"; // 示例二进制字符串
        // 将二进制字符串转换为BigInteger
        BigInteger bigInteger = new BigInteger(binaryString, 2);
        // 将BigInteger转换为BigDecimal
        BigDecimal bigDecimal = new BigDecimal(bigInteger);

        // 把BigInteger转换为二进制字符串
        String binaryStr1 = bigInteger.toString(2);
        Assert.assertEquals(binaryString, binaryStr1);

        // 把BigDecimal转换为二进制字符串
        binaryStr1 = bigDecimal.toBigInteger().toString(2);
        Assert.assertEquals(binaryString, binaryStr1);

        // endregion

        // region BigInteger和BigDecimal相互转换

        // BigInteger转换为BigDecimal
        bigDecimal = new BigDecimal(bigInteger);
        Assert.assertEquals(bigInteger.longValue(), bigDecimal.longValue());

        // BigDecimal转换为BigInteger
        bigInteger = bigDecimal.toBigInteger();
        Assert.assertEquals(bigDecimal.longValue(), bigInteger.longValue());

        // endregion
    }

    /**
     * 空字符串无法解析为 BigInteger，会抛出：
     * java.lang.NumberFormatException: Zero length BigInteger
     */
    @Test
    public void zeroLengthBigInteger() {
        try {
            new BigInteger("");
            Assert.fail("expected NumberFormatException");
        } catch (NumberFormatException e) {
            Assert.assertEquals("Zero length BigInteger", e.getMessage());
        }

        try {
            new BigInteger("", 10);
            Assert.fail("expected NumberFormatException");
        } catch (NumberFormatException e) {
            Assert.assertEquals("Zero length BigInteger", e.getMessage());
        }
    }

    /**
     * 空字符串无法解析为 BigDecimal，同样会抛出 NumberFormatException。
     * 与 BigInteger 不同：JDK 8 下异常 message 为 null（非 "Zero length ..."）；
     * 较新 JDK 在解析阶段可能抛出 message 为 "No digits found." 的同类异常。
     */
    @Test
    public void zeroLengthBigDecimal() {
        try {
            new BigDecimal("");
            Assert.fail("expected NumberFormatException");
        } catch (NumberFormatException e) {
            Assert.assertNull(e.getMessage());
        }

        try {
            new BigDecimal("", MathContext.UNLIMITED);
            Assert.fail("expected NumberFormatException");
        } catch (NumberFormatException e) {
            Assert.assertNull(e.getMessage());
        }

        try {
            new BigDecimal(new char[0], 0, 0);
            Assert.fail("expected NumberFormatException");
        } catch (NumberFormatException e) {
            Assert.assertNull(e.getMessage());
        }
    }
}
