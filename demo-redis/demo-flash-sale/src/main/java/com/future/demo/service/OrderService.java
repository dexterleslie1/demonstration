package com.future.demo.service;

import com.future.demo.dto.PreOrderDto;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;

@Service
public class OrderService {
    public final static String KeyProductStockPrefix = "product:stock:";
    public final static String KeyProductSoldOutPrefix = "product:soldout:";

    private static DefaultRedisScript<Long> defaultRedisScript;

    static {
        defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("flash-sale.lua"));
        defaultRedisScript.setResultType(Long.class);
    }

    OrderService orderServiceProxy = null;
    BlockingQueue<PreOrderDto> blockingQueue = new ArrayBlockingQueue<>(1024 * 1024);
    ExecutorService executor = Executors.newFixedThreadPool(1);
    ExecutorService executorRunner = Executors.newFixedThreadPool(64);

    @PostConstruct
    public void init() {
        executor.submit(() -> {
            while (true) {
                try {
                    PreOrderDto preOrderDto = blockingQueue.take();
                    if (preOrderDto.getProductId() == null) {
                        break;
                    }

                    executorRunner.submit(() -> {
                        try {
                            Long productId = preOrderDto.getProductId();
                            Long userId = preOrderDto.getUserId();
                            Integer amount = preOrderDto.getAmount();
                            orderServiceProxy.createOrderBasedDB(userId, productId, amount);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        // BlockingQueue 退出信号
        this.blockingQueue.put(new PreOrderDto(null, null, null));

        executorRunner.shutdown();
        while (!this.executorRunner.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        // 手动关闭 ExecutorService，否则 JMH 测试结束时会报告错误
        this.executor.shutdown();
        while (!this.executor.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
    }

    @Autowired
    StringRedisTemplate redisTemplate;
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

    // 基于 Redis 实现商品秒杀
    // 结论：性能高于基于数据库的实现 10 倍
    public void createOrderBasedRedis(Long userId, Long productId, Integer amount) throws Exception {
        String productIdStr = String.valueOf(productId);
        String userIdStr = String.valueOf(userId);
        String amountStr = String.valueOf(amount);

        // 库存余量不足时表示后续的所有请求无效
        String key = KeyProductSoldOutPrefix + productIdStr;
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
            throw new Exception("库存不足");
        }

        // 判断库存是否充足、用户是否重复下单
        Long result = this.redisTemplate.execute(defaultRedisScript, Collections.emptyList(), productIdStr, userIdStr, amountStr);
        if (result != null) {
            if (result == 1) {
                key = KeyProductSoldOutPrefix + productIdStr;
                this.redisTemplate.opsForValue().set(key, "");
                throw new Exception("库存不足");
            } else if (result == 2) {
                throw new Exception("用户重复下单");
            } else {
                throw new Exception("下单失败");
            }
        }

        // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
        /*OrderService proxy = (OrderService) AopContext.currentProxy();
        proxy.createOrderInternal(userId, productId, amount);*/
        this.orderServiceProxy = (OrderService) AopContext.currentProxy();
        this.blockingQueue.put(new PreOrderDto(productId, userId, amount));
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
