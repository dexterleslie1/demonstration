package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.common.exception.BusinessException;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.UserMapper;
import com.future.demo.util.OrderRandomlyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class OrderService {
    @Resource
    OrderMapper orderMapper;
    @Resource
    UserMapper userMapper;

    /**
     * 用于协助测试 sharding-jdbc 事务
     *
     * @param userId
     * @param amount
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long userId, BigDecimal amount) throws BusinessException {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        order.setCreateTime(now);
        order.setUserId(userId);
        order.setMerchantId(RandomUtil.randomLong(1, Long.MAX_VALUE));
        order.setTotalAmount(amount);
        order.setTotalCount(10);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        order.setStatus(status);
        order.setDeleteStatus(OrderRandomlyUtil.getDeleteStatusRandomly());
        this.orderMapper.add(order);

        // 扣减用户余额
        int count = this.userMapper.updateDecreaseBalance(userId, amount);
        if (count == 0) {
            throw new BusinessException("余额不足");
        }
    }
}
