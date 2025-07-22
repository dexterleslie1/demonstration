package com.future.demo.util;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderRandomlyUtil {

    final static Status[] statusArray = Status.values();
    final static int statusArrayLength = statusArray.length;

    final static DeleteStatus[] deleteStatusArray = DeleteStatus.values();
    final static int deleteStatusArrayLength = deleteStatusArray.length;

    // 所有订单创建日期的开始时间点
    final static LocalDateTime CreateTimeReferencingTimePoint = LocalDateTime.parse("2015-06-22 19:23:01", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    // 五年的秒数
    final static Long FiveYearsInSeconds = Duration.ofDays(5 * 365).getSeconds();

    /**
     * 随机获取订单状态枚举常量
     *
     * @return
     */
    public static Status getStatusRandomly() {
        return statusArray[RandomUtil.randomInt(0, statusArrayLength)];
    }

    /**
     * 随机获取订单删除状态枚举常量
     *
     * @return
     */
    public static DeleteStatus getDeleteStatusRandomly() {
        return deleteStatusArray[RandomUtil.randomInt(0, deleteStatusArrayLength)];
    }

    /**
     * 随机生成订单创建时间
     *
     * @return
     */
    public static LocalDateTime getCreateTimeRandomly() {
        // 指定时间之后5年内的一个随机时间点
        long randomSeconds = RandomUtil.randomLong(0, FiveYearsInSeconds);
        return CreateTimeReferencingTimePoint.plusSeconds(randomSeconds);
    }
}
