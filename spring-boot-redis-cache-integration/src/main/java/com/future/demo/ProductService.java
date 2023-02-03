package com.future.demo;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final static String KeyLockPrefix = "productStock#";
    @Autowired
    ProductRepository productRepository;
    @Autowired
    RedissonClient redissonClient;

    @Cacheable(cacheNames = "productCache", key = "#id")
    public ProductModel testCachable(Long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    @CacheEvict(cacheNames = "productCache", key = "#id")
    public void testCacheEvict(Long id) {

    }

    @CachePut(cacheNames = "productCache", key = "#id",unless="#result == null")
    public ProductModel testCachePut(Long id) throws Exception {
        String key = KeyLockPrefix + id;
        RLock rLock = this.redissonClient.getLock(key);
        boolean acquired = false;
        try {
            acquired = rLock.tryLock();
            if(!acquired) {
                return null;
            }
            ProductModel productModel = this.productRepository.findById(id).orElse(null);
            if (productModel.getQuantity() < 1) {
                throw new Exception("库存不足");
            }
            this.productRepository.updateDecrease(id, 1);
            return this.productRepository.findById(id).orElse(null);
        } finally {
            if(acquired) {
                try {
                    rLock.unlock();
                } catch (Exception ignored) {

                }
            }
        }
    }
}
