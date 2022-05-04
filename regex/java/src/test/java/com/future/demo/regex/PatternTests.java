package com.future.demo.regex;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class PatternTests {
    @Test
    public void test() {
        //----------------- pattern split方法使用
        Pattern pattern = Pattern.compile("\\d[abc]");
        Assert.assertEquals("\\d[abc]", pattern.pattern());
        String str = "xyz1auvw2brst3copq";

        // pattern matches方法使用
        boolean match = pattern.matcher("uiuriuer3a89990").matches();
        Assert.assertFalse(match);
        match = pattern.matcher("3a").matches();
        Assert.assertTrue(match);

        // 正则split
        String [] strArray = pattern.split(str);
        Assert.assertEquals(4, strArray.length);

        // Pattern.matches方法使用
        match = Pattern.matches("\\d[abc]", "3a");
        Assert.assertTrue(match);
        match = Pattern.matches("\\d[abc]", "uu3a");
        Assert.assertFalse(match);

        // 匹配 /etc/fstab中不以#开头的swap行
        match = Pattern.matches("^(?!#).*swap.*", "/dev/mapper/centos-swap swap                    swap    defaults        0 0");
        Assert.assertTrue(match);
        match = Pattern.matches("^(?!#).*swap.*", "#/dev/mapper/centos-swap swap                    swap    defaults        0 0");
        Assert.assertFalse(match);
    }
}
