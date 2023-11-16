package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CharacterEncodingTests {

    @Test
    public void testUnicode() {
        //#region 演示emoji unicode（unicode 超过 0xffff 时）

        // 通过emoji码点表查询得到 smile emoji 码点如下
        // https://unicode.org/emoji/charts/full-emoji-list.html
        String codePointHexString = "u+1f600";
        // 转换16进制码点值到10进制
        int codePoint = Integer.parseInt(codePointHexString.substring(2), 16);
        // 对codePoint进行utf-16编码
        // https://stackoverflow.com/questions/18380901/how-do-i-convert-unicode-codepoints-to-their-character-representation
        char[] utf16EncodeChars = Character.toChars(codePoint);
        // codePoint utf-16 编码字节数组
        byte[] utf16EncodeBytes = new String(utf16EncodeChars).getBytes(StandardCharsets.UTF_16);
        byte[] utf8EncodeBytes = new String(utf16EncodeChars).getBytes(StandardCharsets.UTF_8);

        // java 源码使用 utf-16 编码，所以 smile emoji 对应的 utf-16 编码如下
        String str = "\ud83d\ude00";
        Assert.assertArrayEquals(str.getBytes(StandardCharsets.UTF_16), utf16EncodeBytes);
        byte[] expectedUtf8EncodeBytes = new byte[]{(byte) 0xf0, (byte) 0x9f, (byte) 0x98, (byte) 0x80};
        Assert.assertArrayEquals(expectedUtf8EncodeBytes, utf8EncodeBytes);

        System.out.println(String.format("%s utf-16 编码为: %s", str, toHexString(utf16EncodeBytes)));
        System.out.println(String.format("%s utf-8 编码为: %s", str, toHexString(utf8EncodeBytes)));

        //#endregion

        //#region 演示中文unicode（unicode 小于 0xffff 时）

        codePoint = 25105;
        utf16EncodeChars = Character.toChars(codePoint);
        utf16EncodeBytes = new String(utf16EncodeChars).getBytes(StandardCharsets.UTF_16);
        utf8EncodeBytes = new String(utf16EncodeChars).getBytes(StandardCharsets.UTF_8);

        str = "\u6211";
        Assert.assertEquals("我", str);

        System.out.println(String.format("%s utf-16 编码为: %s", str, toHexString(utf16EncodeBytes)));
        System.out.println(String.format("%s utf-8 编码为: %s", str, toHexString(utf8EncodeBytes)));

        //#endregion
    }

    String toHexString(byte[] datum) {
        List<String> hexStringList = new ArrayList<>();
        for (byte b : datum) {
            String hexString = toHexString(b);
            hexStringList.add(hexString);
        }
        return String.join(" ", hexStringList);
    }

    String toHexString(byte datum) {
        return String.format("0x%2s", Integer.toHexString(datum & 0XFF)).replaceAll(" ", "0");
    }
}
