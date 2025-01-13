package com.future.demo;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {
    // 缓存更新策略 - 超时剔除策略
    public final static String CacheUpdateStrategyTimeoutEvict = "strategyTimeoutEvict";
    // 缓存更新策略 - 主动更新策略+缓存超时机制
    public final static String CacheUpdateStrategyActiveUpdateAndTimeout = "strategyActiveUpdateAndTimeout";
    // 缓存更新策略 - 主动更新策略+读写锁机制
    public final static String CacheUpdateStrategyActiveUpdateAndReadWriteLock = "strategyActiveUpdateAndReadWriteLock";

    public final static String KeyCacheProductStockPrefix = "productStock#";
    public final static String KeyLockProduct = "productLock#";

    @Resource
    ProductRepository productRepository;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    RedissonClient redissonClient;

    /**
     * @param id
     * @param cacheUpdateStrategy
     * @return
     */
    public int getStock(Long id, String cacheUpdateStrategy) {
        String key = KeyCacheProductStockPrefix + id;
        String productStockStr = this.redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(productStockStr)) {
            return Integer.parseInt(productStockStr);
        }

        RLock rLock = null;
        try {
            if (CacheUpdateStrategyActiveUpdateAndReadWriteLock.equals(cacheUpdateStrategy)) {
                // 获取读锁
                String keyLock = KeyLockProduct + id;
                RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock(keyLock);
                rLock = readWriteLock.readLock();
                rLock.lock();
            }

            ProductModel productModel = this.productRepository.findById(id).orElse(null);
            if (productModel == null) {
                return 0;
            }

            // 模拟先更新数据库，再删除缓存：「请求 A 」去读取数据，但是未在缓存中命中，去数据库读取数据，但是在数据库读取数据之后还没有更新缓存数据之前，「请求 B 」去更新数据库数据，然后删除缓存数据，然后「请求 A 」才更新缓存数据。
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                //
            }

            // 使用缓存超时机制解决 MySQL 和 Redis 数据不一致问题以达到最终一致性
            this.redisTemplate.opsForValue().set(key, String.valueOf(productModel.getQuantity()), 2, TimeUnit.SECONDS);

            return productModel.getQuantity();
        } finally {
            if (rLock != null) {
                rLock.unlock();
            }
        }
    }

    public void decrease(Long id, String cacheUpdateStrategy) throws Exception {
        RLock rLock = null;
        try {
            if (CacheUpdateStrategyActiveUpdateAndReadWriteLock.equals(cacheUpdateStrategy)) {
                // 获取写锁
                String keyLock = KeyLockProduct + id;
                RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock(keyLock);
                rLock = readWriteLock.writeLock();
                rLock.lock();
            }

            ProductModel productModel = this.productRepository.findById(id).orElse(null);
            if (productModel == null || productModel.getQuantity() < 1) {
                throw new Exception("库存不足");
            }
            this.productRepository.updateDecrease(id, 1);

            // 缓存更新策略 - 主动更新策略
            if (CacheUpdateStrategyActiveUpdateAndTimeout.equals(cacheUpdateStrategy)
                    || CacheUpdateStrategyActiveUpdateAndReadWriteLock.equals(cacheUpdateStrategy)) {
                String key = KeyCacheProductStockPrefix + id;
                this.redisTemplate.delete(key);
            }
        } finally {
            if (rLock != null) {
                rLock.unlock();
            }
        }
    }
}
