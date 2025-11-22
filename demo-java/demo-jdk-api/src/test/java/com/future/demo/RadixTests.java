package com.future.demo;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class RadixTests {
    /**
     * x进制字符串转换为字节数组
     */
    @Test
    public void testRadixStringToOrFromByteArray() throws DecoderException {
        // region 二进制字符串转换为字节数组

        String binaryString = "00000001" +
                "00000010" +
                "00000011";
        byte[] bytes = new BigInteger(binaryString, 2).toByteArray();
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals(1, bytes[0]);
        Assert.assertEquals(2, bytes[1]);
        Assert.assertEquals(3, bytes[2]);

        // 测试很大的二进制字符串
        int count = 1000;
        binaryString = "";
        for (int i = 0; i < count; i++) {
            binaryString += "00000001";
        }
        bytes = new BigInteger(binaryString, 2).toByteArray();
        Assert.assertEquals(count, bytes.length);
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(1, bytes[i]);
        }

        // endregion

        // region 字节数组转换为二进制字符串

        BigInteger bigInteger = new BigInteger(bytes);
        String binaryStringFromByteArray = bigInteger.toString(2);
        Assert.assertTrue(binaryString.endsWith(binaryStringFromByteArray));

        // endregion

        // region 16进制字符串转换为字节数组

        String radixString = "FF" +
                "A0" +
                "34";
        bytes = Hex.decodeHex(radixString);
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals(-1, bytes[0]);
        Assert.assertEquals(-96, bytes[1]);
        Assert.assertEquals(52, bytes[2]);

        // 测试很大的16进制字符串
        count = 1000;
        radixString = "";
        for (int i = 0; i < count; i++) {
            radixString += "34";
        }
        bytes = Hex.decodeHex(radixString);
        Assert.assertEquals(count, bytes.length);
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(52, bytes[i]);
        }

        // endregion

        // region 字节数组转换为16进制字符串

        String radixStringFromByteArray = Hex.encodeHexString(bytes);
        Assert.assertTrue(radixString.endsWith(radixStringFromByteArray));

        // endregion

        // region 整数转换为x进制字符串

        // 转换为16进制字符串
        int integer = 0x0F;
        radixString = Integer.toHexString(integer);
        radixString = StringUtils.leftPad(radixString, 2, "0");
        Assert.assertEquals("0f", radixString);

        // 转换为二进制字符串
        integer = 0b1111;
        radixString = Integer.toBinaryString(integer);
        radixString = StringUtils.leftPad(radixString, 8, "0");
        Assert.assertEquals("00001111", radixString);

        // endregion
    }
}
