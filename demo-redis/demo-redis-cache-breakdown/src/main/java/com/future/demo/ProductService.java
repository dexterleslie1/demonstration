//package com.future.demo;
//
//import org.apache.commons.lang3.StringUtils;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class ProductService {
//    public final static String KeyCacheProductStockPrefix = "productStock#";
//
//    @Resource
//    ProductRepository productRepository;
//    @Resource(name = "stringRedisTemplate")
//    StringRedisTemplate redisTemplate;
//
//    public int getStock(Long id) {
//        String key = KeyCacheProductStockPrefix + id;
//        String productStockStr = this.redisTemplate.opsForValue().get(key);
//        if(!StringUtils.isBlank(productStockStr)) {
//            return Integer.parseInt(productStockStr);
//        }
//
//        ProductModel productModel = this.productRepository.findById(id).orElse(null);
//        if(productModel == null) {
//            return 0;
//        }
//
//        // 模拟STW
//        try {
//            TimeUnit.MILLISECONDS.sleep(300);
//        } catch (InterruptedException e) {
//            //
//        }
//
//        this.redisTemplate.opsForValue().set(key, String.valueOf(productModel.getQuantity()));
//        return productModel.getQuantity();
//    }
//
//    public void decrease(Long id) throws Exception {
//        ProductModel productModel = this.productRepository.findById(id).orElse(null);
//        if (productModel == null || productModel.getQuantity() < 1) {
//            throw new Exception("库存不足");
//        }
//        this.productRepository.updateDecrease(id, 1);
//
//        String key = KeyCacheProductStockPrefix + id;
//        this.redisTemplate.delete(key);
//    }
//}
