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

    int userIdBoundaryLimitation;
    int merchantIdBoundaryLimitation;
    int productIdBoundaryLimitation;
    // 随机用户ID数组
    long[] userIdArray;
    // 随机商家ID数组
    long[] merchantIdArray;
    // 随机商品ID数组
    public long[] productIdArray;

    public OrderRandomlyUtil(long totalCount) {
        // 平均一个用户 100 个订单
        userIdBoundaryLimitation = (int) totalCount / 100;
        // 平均一个商家 2000 个订单
        merchantIdBoundaryLimitation = (int) totalCount / 2000;
        // 一个商家下有30个产品
        productIdBoundaryLimitation = merchantIdBoundaryLimitation * 30;

        userIdArray = new long[userIdBoundaryLimitation];
        for (int i = 1; i <= userIdBoundaryLimitation; i++) {
            userIdArray[i - 1] = i;
        }

        merchantIdArray = new long[merchantIdBoundaryLimitation];
        for (int i = 1; i <= merchantIdBoundaryLimitation; i++) {
            merchantIdArray[i - 1] = i;
        }

        productIdArray = new long[productIdBoundaryLimitation];
        for (int i = 1; i <= productIdBoundaryLimitation; i++) {
            productIdArray[i - 1] = i;
        }
    }

    public int getProductIdBoundaryLimitation() {
        return this.productIdBoundaryLimitation;
    }
    public int getMerchantIdBoundaryLimitation() {
        return this.merchantIdBoundaryLimitation;
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
        return userIdArray[RandomUtil.randomInt(0, userIdBoundaryLimitation)];
    }

    /**
     * 随机获取商家ID
     *
     * @return
     */
    public Long getMerchantIdRandomly() {
        return merchantIdArray[RandomUtil.randomInt(0, merchantIdBoundaryLimitation)];
    }

    /**
     * 随机获取商品ID
     *
     * @return
     */
    public Long getProductIdRandomly() {
        return productIdArray[RandomUtil.randomInt(0, productIdBoundaryLimitation)];
    }
}
