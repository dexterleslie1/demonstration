package com.future.demo;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.junit.Assert;
import org.junit.Test;

public class ZhConverterUtilTests {
    @Test
    public void test() {
        String str = "陈学";
        String str1 = ZhConverterUtil.toSimple(str);
        Assert.assertEquals(str, str1);

        str1 = ZhConverterUtil.toTraditional(str);
        Assert.assertEquals("陳學", str1);

        String strTraditional = "陳學";
        str1 = ZhConverterUtil.toTraditional(strTraditional);
        Assert.assertEquals(strTraditional, str1);
        str1 = ZhConverterUtil.toSimple(strTraditional);
        Assert.assertEquals(str, str1);
    }
}
