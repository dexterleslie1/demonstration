package com.future.demo.java.commons.lang;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilsTests {
    @Test
    public void test() throws ParseException {
        String dateTimeStr = "2022-06-03 23:21:22";
        Date dateTime =
                DateUtils.parseDate(
                        dateTimeStr, "yyyy-MM-dd HH:mm:ss");
        Date dateTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStr);
        Assert.assertEquals(dateTime2, dateTime);
    }
}
