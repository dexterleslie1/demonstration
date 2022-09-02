package com.future.demo.regex;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;


public class RegexTests {
    @Test
    public void test() {
        // --------------------- 基本通配符 ---------------------------
        // \d：匹配一个数字字符，既0-9其中一个
        String str = "jf1ajldreur7aieureuraoeuriue";
        str = str.replaceAll("\\da", "--");
        Assert.assertEquals("jf--jldreur--ieureuraoeuriue", str);

        // \D：匹配一个非数字字符
        str = "jf1ajldreur7aieureuraoeuriue";
        str = str.replaceAll("\\Da", "--");
        Assert.assertEquals("jf1ajldreur7aieureu--oeuriue", str);

        // \w：匹配任意一个数字、字母和下划线
        str = "你1好2*#abc以及A_B&&";
        str = str.replaceAll("\\w", "-");
        Assert.assertEquals("你-好-*#---以及---&&", str);

        // \W：匹配所有\w不匹配的字符
        str = "你1好2*#abc以及A_B&&";
        str = str.replaceAll("\\W", "-");
        Assert.assertEquals("-1-2--abc--A_B--", str);

        // \s：匹配空白字符，包括空格、制表符、换页符等
        // \S：匹配所有\s不匹配的字符
        // .：匹配除换行符(\n)之外的所有字符，1个 . 仅能匹配1个字符
        str = "7ab3ega8wgqea%6qmv";
        str = str.replaceAll("a.\\d", "--");
        Assert.assertEquals("7--ega8wgqe--qmv", str);

        // --------------------- 自定义通配符 ---------------------------
        // []：自定义通配符，匹配中括号中指定的任意一个字符
        str = "abcd7ab3exga]8wgqea%6qmv-ku";
        // 匹配abcd字母中任意一个
        str = str.replaceAll("[abcd]", "*");
        Assert.assertEquals("****7**3exg*]8wgqe*%6qmv-ku", str);

        str = "abcd7ab3exga]8wgqea%6qmv-ku";
        // 同上，只是上面简写方式
        str = str.replaceAll("[a-d]", "*");
        Assert.assertEquals("****7**3exg*]8wgqe*%6qmv-ku", str);

        str = "abcd7ab3exga]8wgqea%6qmv-ku";
        // 匹配abcdxyz字母中任意一个
        str = str.replaceAll("[a-dx-z]", "*");
        Assert.assertEquals("****7**3e*g*]8wgqe*%6qmv-ku", str);

        str = "abcd7ab3exga]8wgqea%6qmv-ku";
        // 匹配abcdqvxyz字母中任意一个
        str = str.replaceAll("[a-dqvx-z]", "*");
        Assert.assertEquals("****7**3e*g*]8wg*e*%6*m*-ku", str);

        str = "abcd7ab3exga]8wgqea%6qmv-ku";
        // 匹配除abcd外的任意一个字符
        str = str.replaceAll("[^a-d]", "*");
        Assert.assertEquals("abcd*ab****a******a********", str);

        // --------------------- 还原特殊符号（特殊符号失效区） ---------------------------
        str = "a..[a]..aaaab";
        // 匹配除abcd外的任意一个字符
        str = str.replaceAll("\\.\\.\\[a\\]\\.\\.a", "*");
        Assert.assertEquals("a*aaab", str);

        str = "a..[a]..aaaab";
        // 匹配除abcd外的任意一个字符
        str = str.replaceAll("\\Q..[a]..a\\E", "*");
        Assert.assertEquals("a*aaab", str);

        // --------------------- 出现次数 ---------------------------
        str = "123a2345b";
        // 匹配连续三次数字
        str = str.replaceAll("\\d{3}", "*");
        Assert.assertEquals("*a*5b", str);

        str = "123a2345b";
        // 匹配连续三次或者四次数字
        str = str.replaceAll("\\d{3,4}", "*");
        Assert.assertEquals("*a*b", str);

        str = "1a2345b";
        // 匹配至少出现两次数字
        str = str.replaceAll("\\d{2,}", "*");
        Assert.assertEquals("1a*b", str);

        str = "1a2345a3b";
        // 匹配 "a(没有或者一个数字)3"
        str = str.replaceAll("a\\d?3", "*");
        Assert.assertEquals("1*45*b", str);

        str = "1a234a57935a3b";
        // 匹配 "a(没有或者一个数字)3"
        str = str.replaceAll("a\\d?3", "*");
        Assert.assertEquals("1*4a57935*b", str);

        str = "1a234a57935a3b";
        // 匹配 "a(没有或者多个数字)3"
        str = str.replaceAll("a\\d*3", "*");
        Assert.assertEquals("1*4*5*b", str);

        str = "1a234a57935a3b";
        // 匹配 "a(一个或者多个数字)3"
        str = str.replaceAll("a\\d+3", "*");
        Assert.assertEquals("1*4*5a3b", str);

        str = "ababbbc";
        str = str.replaceAll("ab+", "*");
        Assert.assertEquals("**c", str);

        // --------------------- 贪婪模式和非贪婪模式 ---------------------------
        str = "dxxxdxxxd";
        // 贪婪模式
        str = str.replaceAll("d.+", "*");
        Assert.assertEquals("*", str);

        str = "dxxxdxxxd";
        // 非贪婪模式
        str = str.replaceAll("d.+?", "*");
        Assert.assertEquals("*xx*xxd", str);

        // --------------------- 定位符的使用 ---------------------------
        str = "aaayyyaaayyyaaa";
        // 匹配开头连续三个a
        str = str.replaceAll("^aaa", "*");
        Assert.assertEquals("*yyyaaayyyaaa", str);

        str = "aaayyyaaayyyaaa";
        // 匹配连续三个a结尾
        str = str.replaceAll("aaa$", "*");
        Assert.assertEquals("aaayyyaaayyy*", str);

        str = "aaayyyaaayyyaaa";
        // 匹配开头连续三个a或者连续三个a结尾
        str = str.replaceAll("^aaa|aaa$", "*");
        Assert.assertEquals("*yyyaaayyy*", str);

        // 匹配字母和数字组合
        Pattern pattern = Pattern.compile("[a-z0-9]+");
        Assert.assertFalse(pattern.matcher("").matches());
        Assert.assertTrue(pattern.matcher("123").matches());
        Assert.assertTrue(pattern.matcher("123abc").matches());
        Assert.assertTrue(pattern.matcher("1a2b3c").matches());
        Assert.assertTrue(pattern.matcher("a1b2c3").matches());
        Assert.assertFalse(pattern.matcher("a1b2c3,.").matches());
        Assert.assertFalse(pattern.matcher("a1b,2c3").matches());
        Assert.assertFalse(pattern.matcher(".a1b2c3").matches());
    }
}
