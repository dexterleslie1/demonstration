package com.future.demo.util;

import cn.hutool.core.util.RandomUtil;

public class Util {
    /**
     * 性能测试用户 ID 总数
     */
    public final static int PerfTestDatumUserIdTotalCount = 10000;
    /**
     * 性能测试用户 ID 开始数
     */
    public final static int PerfTestDatumUserIdStart = 10;
    /**
     * 性能测试用户余额
     */
    public final static int PerfTestDatumUserBalance = 999999999;
    /**
     * 性能测试商品 ID 总数
     */
    public final static int PerfTestDatumProductIdTotalCount = 10000;
    /**
     * 性能测试商品 ID 开始数
     */
    public final static int PerfTestDatumProductIdStart = 10;
    /**
     * 性能测试商品库存
     */
    public final static int PerfTestDatumProductStockAmount = 999999999;

    /**
     * 随机抽取用户 ID
     *
     * @return
     */
    public static Long randomUserId() {
        return RandomUtil.randomLong(PerfTestDatumUserIdStart, PerfTestDatumProductIdStart + PerfTestDatumUserIdTotalCount);
    }

    /**
     * 随机抽取商品 ID
     *
     * @return
     */
    public static Long randomProductId() {
        return RandomUtil.randomLong(PerfTestDatumProductIdStart, PerfTestDatumProductIdStart + PerfTestDatumProductIdTotalCount);
    }
}
