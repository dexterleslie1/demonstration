package com.future.demo;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUnitTests {
    @Test
    public void testSleep() throws InterruptedException {
        int count = 5;
        System.out.println("开始测试sleep seconds");
        for(int i=0; i<count; i++) {
            Date timeNow = new Date();
            String dateStr = DateFormatUtils.format(timeNow, "yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("时间: " + dateStr);

            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("结束测试sleep seconds");
        System.out.println();
        System.out.println();

        System.out.println("开始测试sleep milliseconds");
        for(int i=0; i<count; i++) {
            Date timeNow = new Date();
            String dateStr = DateFormatUtils.format(timeNow, "yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("时间: " + dateStr);

            TimeUnit.MILLISECONDS.sleep(100);
        }
        System.out.println("结束测试sleep milliseconds");
        System.out.println();
        System.out.println();

        System.out.println("开始测试sleep microseconds");
        for(int i=0; i<count; i++) {
            Date timeNow = new Date();
            String dateStr = DateFormatUtils.format(timeNow, "yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("时间: " + dateStr);

            TimeUnit.MICROSECONDS.sleep(200*1000);
        }
        System.out.println("结束测试sleep microseconds");
        System.out.println();
        System.out.println();

        System.out.println("开始测试sleep nanoseconds");
        for(int i=0; i<count; i++) {
            Date timeNow = new Date();
            String dateStr = DateFormatUtils.format(timeNow, "yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("时间: " + dateStr);

            TimeUnit.NANOSECONDS.sleep(300*1000*1000);
        }
        System.out.println("结束测试sleep nanoseconds");
        System.out.println();
        System.out.println();
    }
}
