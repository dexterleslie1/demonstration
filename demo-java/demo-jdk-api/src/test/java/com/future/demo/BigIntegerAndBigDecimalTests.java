package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

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
}
