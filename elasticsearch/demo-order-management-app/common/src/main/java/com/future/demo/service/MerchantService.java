package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MerchantService {
    /**
     * 商家总数为100万
     */
    public final static int TotalCount = 1000000;

    /**
     * 辅助测试随机获取商家ID
     *
     * @return
     */
    public Long getIdRandomly() {
        return RandomUtil.randomLong(1, TotalCount + 1);
    }
}
