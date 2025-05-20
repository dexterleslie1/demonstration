package com.future.demo.jdk8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DateTimeTests {
    @Test
    public void test() throws InterruptedException {
        // region 旧版本日期时间 API 存在的问题

        // 1、设计很差：在 java.util 和 java.sql 的包中都有日期类，java.util.Date 同时包含日期和时间，而 java.sql.Date 仅包含日期。此外用于格式化和解析的类在 java.text 包中定义。
        // 2、非线程安全： java.util.Date 是非线程安全的，所有的日期类都是可变的。这是 Java 日期类最大的问题之一。
        // 3、时区处理麻烦：日期类并不提供国际化，没有时区支持，因此 Java 引入了 java.util.Calendar 和 java.util.TimeZone 类，但他们同样存在上述所有问题。

        // 输出为 Thu Oct 23 00:00:00 CST 3890 时间不合理
        Date date = new Date(1990, 9, 23);
        System.out.println(date);

        // 线程不安全
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                try {
                    Date date1 = simpleDateFormat.parse("2019-09-23");
                    System.out.println(date1);
                } catch (Exception ex) {

                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        // endregion

        // region `java.time.LocalDate`: 表示日期，例如 2024-03-08，不包含时间信息。

        // 当前时间
        LocalDate localDate = LocalDate.now();
        System.out.println("localData=" + localDate);

        // 创建指定时间
        localDate = LocalDate.of(2019, 5, 12);
        System.out.println("localDate=" + localDate);

        // endregion

        // region **`java.time.LocalTime`:** 表示时间，例如 10:30:15，不包含日期信息。

        LocalTime localTime = LocalTime.now();
        System.out.println("localTime = " + localTime);

        localTime = LocalTime.of(13, 12, 15);
        System.out.println("localTime = " + localTime);

        // endregion

        // region **`java.time.LocalDateTime`:**  表示日期和时间，例如 2024-03-08T10:30:15。

        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("localDateTime = " + localDateTime);

        localDateTime = LocalDateTime.of(2015, 2, 5, 13, 25, 21);
        System.out.println("localDateTime = " + localDateTime);

        // endregion

        // region 修改日期和时间

        // 使用设置方式修改日期和时间
        localDate = localDate.withYear(2011);
        System.out.println("localDate = " + localDate);

        // 使用增加或者减去方式修改日期和时间
        localDate = localDate.plusYears(10);
        System.out.println("localDate = " + localDate);

        // endregion

        // region 比较日期和时间

        localDateTime = LocalDateTime.of(2015, 2, 23, 13, 22, 11);
        LocalDateTime now = LocalDateTime.now();
        System.out.println("localDateTime.isAfter(now) = " + localDateTime.isAfter(now));
        System.out.println("localDateTime.isBefore(now) = " + localDateTime.isBefore(now));
        System.out.println("localDateTime.isEqual(now) = " + localDateTime.isEqual(now));

        // 截断毫秒以比较时间
        now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS /* 截断到秒 */);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localDateTimeStr = now.format(dateTimeFormatter);
        localDateTime = LocalDateTime.parse(localDateTimeStr, dateTimeFormatter);
        System.out.println("localDateTime.equals(now) = " + localDateTime.equals(now));

        // endregion

        // region 时间格式化和解析

        // 时间格式化
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        localDateTime = LocalDateTime.now();
        localDateTimeStr = localDateTime.format(dateTimeFormatter);
        System.out.println("localDateTimeStr = " + localDateTimeStr);

        // 解析日期和时间
        localDateTime = LocalDateTime.parse(localDateTimeStr, dateTimeFormatter);
        System.out.println("localDateTime = " + localDateTime);

        // endregion

        // region Instant 类

        // 获取当前时间戳
        Instant nowInstant = Instant.now();
        System.out.println("当前时间戳: " + nowInstant);

        // 将 Instant 转换为 Date 对象
        Date date1 = Date.from(nowInstant);
        System.out.println("转换为 Date 对象: " + date1);

        // 将 Date 对象转换为 Instant 对象
        Instant instantFromDate = date1.toInstant();
        System.out.println("从 Date 对象转换回 Instant 对象: " + instantFromDate);


        // 将时间戳转换为特定时区的日期时间
        //  注意：Instant 本身不包含时区信息, 需要使用 ZonedDateTime 来表示特定时区的日期和时间
        java.time.ZonedDateTime zonedDateTime = nowInstant.atZone(java.time.ZoneId.of("America/New_York"));
        System.out.println("纽约时间: " + zonedDateTime);

        //  计算时间差
        Instant later = Instant.now().plusSeconds(60); // 60秒后
        java.time.Duration duration = java.time.Duration.between(nowInstant, later);
        System.out.println("时间差: " + duration);

        // Instant 和 LocalDateTime 相互转换
        // https://stackoverflow.com/questions/52264768/how-to-convert-from-instant-to-localdate
        Instant instant = Instant.now();
        localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        Instant instant1 = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
        Assert.assertEquals(instant, instant1);

        // endregion

        // region Duration/Period 类计算日期和时间差

        LocalTime localTime1 = LocalTime.of(13, 23, 35);
        LocalTime localTime2 = LocalTime.now();
        Duration duration1 = Duration.between(localTime2, localTime1);
        System.out.println("相差秒数 = " + duration1.toSeconds());

        LocalDate localDate1 = LocalDate.of(2011, 2, 23);
        LocalDate localDate2 = LocalDate.now();
        Period period = Period.between(localDate1, localDate2);
        System.out.println("相差天数 = " + period.getDays());

        // endregion

        // region 时间调整器

        localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.with(temporal -> {
            // 设置下个月第一天
            temporal = ((LocalDateTime) temporal).plusMonths(1).withDayOfMonth(1);
            return temporal;
        });
        System.out.println("localDateTime = " + localDateTime);

        localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfNextMonth());
        System.out.println("localDateTime = " + localDateTime);

        // endregion

        // region 日期时间的时区

        // 获取所有时区
        ZoneId.getAvailableZoneIds().forEach(System.out::println);

        ZonedDateTime zonedDateTime1 = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        System.out.println("zonedDateTime1 = " + zonedDateTime1);

        // 修改时区
        zonedDateTime1 = zonedDateTime1.withZoneSameInstant(ZoneId.of("America/Vancouver"));
        System.out.println("zonedDateTime1 = " + zonedDateTime1);
        zonedDateTime1 = zonedDateTime1.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        System.out.println("zonedDateTime1 = " + zonedDateTime1);

        // endregion
    }
}
