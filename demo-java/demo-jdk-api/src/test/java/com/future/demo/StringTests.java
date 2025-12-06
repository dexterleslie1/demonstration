package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringTests {
    /**
     * Java to get Chinese and English mixed character length
     * https://titanwolf.org/Network/Articles/Article?AID=ab5275e2-a70b-45c9-90e3-e3014336e92e#gsc.tab=0
     */
    @Test
    public void testStringLength() {
        String str = "abc";
        int length = getMixedLength(str);
        Assert.assertEquals(3, length);

        str = "中文文";
        length = getMixedLength(str);
        Assert.assertEquals(6, length);

        str = "中文文abcd";
        length = getMixedLength(str);
        Assert.assertEquals(10, length);
    }

    private int getMixedLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* Get the length of the field value. If it contains Chinese characters, the length of each Chinese character is 2, otherwise it is 1 */
        for (int i = 0; i < value.length(); i++) {
            /* Get a character */
            String temp = value.substring(i, i + 1);
            /* Determine whether it is a Chinese character */
            if (temp.matches(chinese)) {
                /* Chinese character length is 2 */
                valueLength += 2;
            } else {
                /* Other characters are 1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    @Test
    public void testSplit() {
        // 只从第一个出现的分隔符分隔字符串
        // https://stackoverflow.com/questions/18462826/split-string-only-on-first-instance-java/18462905

        String str = "obs-default-epu-555-33/2021-08-20/uuu-01中文.jpg";
        String strArr[] = str.split("/", 2);
        System.out.println(String.join(",", Arrays.asList(strArr)));
    }
}
