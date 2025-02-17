package com.future.demo.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.DeleteStatus;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;

import java.math.BigDecimal;
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

    // 随机用户ID数组
    long[] userIdArrayRandomly;
    // 随机商家ID数组
    long[] merchantIdArrayRandomly;

    public OrderRandomlyUtil(long totalCount) {
        // 平均一个用户 100 个订单
        userIdArrayRandomly = new long[(int) totalCount / 100];
        // 平均一个商家 2000 个订单
        merchantIdArrayRandomly = new long[(int) totalCount / 2000];

        for (int i = 0; i < userIdArrayRandomly.length; i++) {
            userIdArrayRandomly[i] = RandomUtil.randomLong(1, Long.MAX_VALUE);
        }

        for (int i = 0; i < merchantIdArrayRandomly.length; i++) {
            merchantIdArrayRandomly[i] = RandomUtil.randomLong(1, Long.MAX_VALUE);
        }
    }

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

    /**
     * 随机获取用户ID
     *
     * @return
     */
    public Long getUserIdRandomly() {
        return userIdArrayRandomly[RandomUtil.randomInt(0, userIdArrayRandomly.length)];
    }

    /**
     * 随机获取商家ID
     *
     * @return
     */
    public Long getMerchantIdRandomly() {
        return merchantIdArrayRandomly[RandomUtil.randomInt(0, merchantIdArrayRandomly.length)];
    }

    /**
     * 随机创建订单
     *
     * @return
     */
    public Order createRandomly() {
        Order order = new Order();
        order.setId(IdUtil.getSnowflake().nextId());
        order.setCreateTime(getCreateTimeRandomly());
        order.setUserId(getUserIdRandomly());
        order.setMerchantId(getMerchantIdRandomly());
        order.setTotalAmount(new BigDecimal(1000));
        order.setTotalCount(10);
        order.setStatus(getStatusRandomly());
        order.setDeleteStatus(getDeleteStatusRandomly());
        return order;
    }
}
