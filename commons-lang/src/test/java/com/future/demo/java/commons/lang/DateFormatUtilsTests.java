package com.future.demo.java.commons.lang;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.Date;

@Slf4j
public class DateFormatUtilsTests {
    @Test
    public void format() {
        Date timeNow = new Date();
        String dateString = DateFormatUtils.format(timeNow, "yyyy-MM-dd");
        log.debug("dateString=" + dateString);
    }
}
