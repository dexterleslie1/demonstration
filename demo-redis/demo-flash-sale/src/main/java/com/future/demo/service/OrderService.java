package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.dto.PreOrderDto;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;

@Service
@Slf4j
public class OrderService {
    //    public final static String KeyProductStockPrefix = "product:stock:";
    public final static String KeyproductStockWithHashTag = "product{%s}:stock";
    //    public final static String KeyProductPurchaseRecordPrefix = "product:purchase:";
    public final static String KeyProductPurchaseRecordWithHashTag = "product{%s}:purchase";

    public final static String KeyProductSoldOutPrefix = "product:soldout:";

    public final static int ProductCount = 100;

    static DefaultRedisScript<Long> defaultRedisScript = null;
    static String Script = null;

    static {
        try {
            defaultRedisScript = new DefaultRedisScript<>();
            /*defaultRedisScript.setLocation(new ClassPathResource("flash-sale.lua"));*/
            ClassPathResource classPathResource = new ClassPathResource("flash-sale.lua");
            String script = StreamUtils.copyToString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
            classPathResource.getInputStream().close();
            defaultRedisScript.setScriptText(script);
            defaultRedisScript.setResultType(Long.class);

            Script = defaultRedisScript.getScriptAsString();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            System.exit(1);
        }
    }

    OrderService orderServiceProxy = null;
    BlockingQueue<PreOrderDto> blockingQueue = new ArrayBlockingQueue<>(1024 * 1024);
    ExecutorService executor = Executors.newFixedThreadPool(1);
    ExecutorService executorRunner = Executors.newFixedThreadPool(64);

    @Resource
    RedisClusterCommands<String, String> sync;

    @PostConstruct
    public void init() {
        /*executor.submit(() -> {
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
        });*/

        // region 准备协助基准测试的数据

        this.orderMapper.deleteAll();

        for (long i = 1; i <= ProductCount; i++) {
            // 准备 redis 数据辅助基于缓存的测试
            Integer productStock = 1000000000;
            String keyProductStock = String.format(OrderService.KeyproductStockWithHashTag, i);
            this.redisTemplate.opsForValue().set(keyProductStock, String.valueOf(productStock));
            String keyProductPurchaseRecord = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, i);
            this.redisTemplate.delete(keyProductPurchaseRecord);
            String key = OrderService.KeyProductSoldOutPrefix + i;
            this.redisTemplate.delete(key);

            // 准备 db 数据辅助基于数据库的测试
            this.productMapper.delete(i);
            ProductModel productModel = new ProductModel();
            productModel.setId(i);
            productModel.setName("产品" + i);
            productModel.setStock(productStock);
            this.productMapper.insert(productModel);
        }

        // endregion
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
            throw new BusinessException("库存不足");
        }

        // 使用分布式锁，防止用户重复下单
        String lockKey = "lock:" + userId + ":" + productId;
        RLock lock = null;
        boolean acquired = false;
        try {
            lock = redissonClient.getLock(lockKey);
            acquired = lock.tryLock();
            if (!acquired) {
                throw new BusinessException("用户重复下单");
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

    // 基于 Redis+Lua脚本 实现商品秒杀，注意：只能够与 Redis Standalone 模式配合运行，与 Redis 其他模式配合运行会报错。
    // 结论：性能高于基于数据库的实现的 3 倍
    public void createOrderBasedRedisWithLuaScript(Long userId, Long productId, Integer amount) throws Exception {
        String productIdStr = String.valueOf(productId);
        String userIdStr = String.valueOf(userId);
        String amountStr = String.valueOf(amount);

        // 库存余量不足时表示后续的所有请求无效
        /*String key = KeyProductSoldOutPrefix + productIdStr;
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
            throw new BusinessException("库存不足");
        }*/

        // 判断库存是否充足、用户是否重复下单
        Long result = this.redisTemplate.execute(defaultRedisScript, Collections.singletonList(productIdStr), productIdStr, userIdStr, amountStr);
        /*Long result = sync.eval(Script, ScriptOutputType.INTEGER, new String[]{productIdStr}, productIdStr, userIdStr, amountStr);*/
        if (result != null) {
            if (result == 1) {
                String key = KeyProductSoldOutPrefix + productIdStr;
                this.redisTemplate.opsForValue().set(key, "");
                /*sync.set(key, "");*/
                throw new BusinessException("库存不足");
            } else if (result == 2) {
                throw new BusinessException("用户重复下单");
            } else {
                throw new BusinessException("下单失败");
            }
        }

        // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
        /*OrderService proxy = (OrderService) AopContext.currentProxy();
        proxy.createOrderInternal(userId, productId, amount);*/
//        if (this.orderServiceProxy == null) {
//            this.orderServiceProxy = (OrderService) AopContext.currentProxy();
//        }
//        this.blockingQueue.put(new PreOrderDto(productId, userId, amount));
    }

    public void createOrderBasedRedisWithoutLuaScript(Long userId, Long productId, Integer amount) throws Exception {
//        String productIdStr = String.valueOf(productId);
//        String userIdStr = String.valueOf(userId);
//
//        // 库存余量不足时表示后续的所有请求无效
//        String key = KeyProductSoldOutPrefix + productIdStr;
//        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
//            throw new BusinessException("库存不足");
//        }
//
//        // region 判断库存是否充足、用户是否重复下单
//        RLock rLock = null;
//        boolean acquired = false;
//        try {
//            rLock = this.redissonClient.getLock(productIdStr);
//            acquired = rLock.tryLock(5, TimeUnit.SECONDS);
//            if (!acquired) {
//                throw new BusinessException("服务器被挤爆了！！！");
//            }
//
//            String productStockKey = OrderService.KeyProductStockPrefix + productIdStr;
//            String stockStr = this.redisTemplate.opsForValue().get(productStockKey);
//            int stock = Integer.parseInt(Objects.requireNonNull(stockStr));
//            if (stock < amount) {
//                String productSoldOutKey = KeyProductSoldOutPrefix + productIdStr;
//                this.redisTemplate.opsForValue().set(productSoldOutKey, "");
//                throw new BusinessException("库存不足");
//            }
//
//            String productPurchaseRecordKey = OrderService.KeyProductPurchaseRecordPrefix + productIdStr;
//            Boolean member = this.redisTemplate.opsForSet().isMember(productPurchaseRecordKey, userIdStr);
//            if (Boolean.TRUE.equals(member)) {
//                throw new BusinessException("用户重复下单");
//            }
//
//            this.redisTemplate.opsForValue().decrement(productStockKey, amount);
//            this.redisTemplate.opsForSet().add(productPurchaseRecordKey, userIdStr);
//        } finally {
//            if (rLock != null && acquired) {
//                rLock.unlock();
//            }
//        }
//
//        // endregion

        // 获取 OrderService 的代理对象，否则 createOrderInternal 方法的 @Transactional 注解不生效
        /*if (this.orderServiceProxy == null) {
            this.orderServiceProxy = (OrderService) AopContext.currentProxy();
        }
        this.blockingQueue.put(new PreOrderDto(productId, userId, amount));*/
    }

    // 抛出异常后回滚事务
    @Transactional(rollbackFor = Exception.class)
    public void createOrderInternal(Long userId, Long productId, Integer amount) throws Exception {
        // 判断用户是否重复下单
        OrderModel orderModel = this.orderMapper.getByUserIdAndProductId(userId, productId);
        if (orderModel != null) {
            throw new BusinessException("用户重复下单");
        }

        // 创建订单
        orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setProductId(productId);
        orderModel.setAmount(amount);
        orderModel.setCreateTime(new Date());
        int count = this.orderMapper.insert(orderModel);
        if (count <= 0) {
            throw new BusinessException("创建订单失败");
        }

        // 扣减库存
        count = this.productMapper.decreaseStock(productId, amount);
        if (count <= 0) {
            throw new BusinessException("扣减库存失败");
        }
    }
}
