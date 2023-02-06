package com.future.demo;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {
    public final static String KeyCacheProductStockPrefix = "productStock#";
    public final static String KeyLockProduct = "productLock#";

    @Resource
    ProductRepository productRepository;
    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate redisTemplate;
    @Resource
    RedissonClient redissonClient;

    public int getStock(Long id) throws InterruptedException {
        String key = KeyCacheProductStockPrefix + id;
        String productStockStr = this.redisTemplate.opsForValue().get(key);
        if(!StringUtils.isBlank(productStockStr)) {
            return Integer.parseInt(productStockStr);
        }

        String keyLock = KeyLockProduct + id;
        RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock(keyLock);
        RLock rLock = readWriteLock.readLock();
        try {
            rLock.lock();
            ProductModel productModel = this.productRepository.findById(id).orElse(null);
            if (productModel == null) {
                return 0;
            }

            this.redisTemplate.opsForValue().set(key, String.valueOf(productModel.getQuantity()));
            return productModel.getQuantity();
        } finally {
            try {
                rLock.unlock();
            } catch (Exception ignored) {

            }
        }
    }

    public void decrease(Long id) throws Exception {
        String keyLock = KeyLockProduct + id;
        RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock(keyLock);
        RLock rLock = readWriteLock.writeLock();
        try {
            rLock.lock();
            ProductModel productModel = this.productRepository.findById(id).orElse(null);
            if (productModel == null || productModel.getQuantity() < 1) {
                throw new Exception("库存不足");
            }
            this.productRepository.updateDecrease(id, 1);

            String key = KeyCacheProductStockPrefix + id;
            this.redisTemplate.delete(key);
        } finally {
            try {
                rLock.unlock();
            } catch (Exception ignored) {

            }
        }
    }
}
