package com.future.demo.service;

import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class OrderService {
    @Resource
    OrderMapper orderMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    RedissonClient redissonClient;

    // 基于数据库实现商品秒杀
    public void createOrderBasedDB(Long userId, Long productId, Integer amount) throws Exception {
        // 判断库存是否充足
        ProductModel productModel = this.productMapper.getById(productId);
        if (productModel.getStock() <= 0) {
            throw new Exception("库存不足");
        }

        // 使用分布式锁，防止用户重复下单
        String lockKey = "lock:" + userId + ":" + productId;
        RLock lock = null;
        boolean acquired = false;
        try {
            lock = redissonClient.getLock(lockKey);
            acquired = lock.tryLock();
            if (!acquired) {
                throw new Exception("用户重复下单");
            }

            // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
            OrderService proxy = (OrderService) AopContext.currentProxy();
            proxy.createOrderInternal(userId, productId, amount);
        } finally {
            if (lock != null && acquired) {
                lock.unlock();
            }
        }
    }

    // 抛出异常后回滚事务
    @Transactional(rollbackFor = Exception.class)
    public void createOrderInternal(Long userId, Long productId, Integer amount) throws Exception {
        // 判断用户是否重复下单
        OrderModel orderModel = this.orderMapper.getByUserIdAndProductId(userId, productId);
        if (orderModel != null) {
            throw new Exception("用户重复下单");
        }

        // 创建订单
        orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setProductId(productId);
        orderModel.setAmount(amount);
        orderModel.setCreateTime(new Date());
        int count = this.orderMapper.insert(orderModel);
        if (count <= 0) {
            throw new Exception("创建订单失败");
        }

        // 扣减库存
        count = this.productMapper.decreaseStock(productId, amount);
        if (count <= 0) {
            throw new Exception("扣减库存失败");
        }
    }
}
