package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class Tests {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductService productService;
    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate redisTemplate;

    @Before
    public void setup() {
        this.productRepository.deleteAll();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.flushDb();
            return "ok";
        });
    }

    /**
     * 下面模拟 1 条线程减少库存，多条并发线程读取商品数据场景导致 Redis 和 MySQL 数据不一致问题
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws Exception {
        // 创建一个商品
        ProductModel productModel = new ProductModel();
        productModel.setQuantity(100);
        productModel.setCreateTime(new Date());
        this.productRepository.save(productModel);

        Long id = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(0).getId();

        // region 缓存更新策略 - 超时剔除策略

        this.productService.getStock(id, ProductService.CacheUpdateStrategyTimeoutEvict);
        this.productService.decrease(id, ProductService.CacheUpdateStrategyTimeoutEvict);

        // 验证 Redis 中商品库存和数据库中商品库存不一致
        int stockInRedis = this.productService.getStock(id, ProductService.CacheUpdateStrategyTimeoutEvict);
        productModel = this.productRepository.findById(id).orElse(null);
        Assert.assertNotEquals(stockInRedis, productModel.getQuantity());

        // Redis 缓存超时剔除后数据会最终一致
        TimeUnit.SECONDS.sleep(2);
        stockInRedis = this.productService.getStock(id, ProductService.CacheUpdateStrategyTimeoutEvict);
        productModel = this.productRepository.findById(id).orElse(null);
        Assert.assertEquals(stockInRedis, productModel.getQuantity());

        // endregion

        // 等待缓存数据超时
        TimeUnit.SECONDS.sleep(3);

        // region 缓存更新策略 - 主动更新策略+缓存超时机制解决数据不一致问题
        ExecutorService executorService = Executors.newCachedThreadPool();

        int concurrentThreads = 128;
        // 多条并发线程读取商品数据
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                this.productService.getStock(id, ProductService.CacheUpdateStrategyActiveUpdateAndTimeout);
            });
        }

        // 1 条线程减少库存
        executorService.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                //
            }
            try {
                this.productService.decrease(id, ProductService.CacheUpdateStrategyActiveUpdateAndTimeout);
            } catch (Exception e) {
                //
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        // 验证 Redis 中商品库存和数据库中商品库存不一致
        stockInRedis = this.productService.getStock(id, ProductService.CacheUpdateStrategyActiveUpdateAndTimeout);
        productModel = this.productRepository.findById(id).orElse(null);
        Assert.assertNotEquals(stockInRedis, productModel.getQuantity());
        // log.debug("Redis {} 数据库 {}", stockInRedis, productModel.getQuantity());

        // Redis 缓存有缓存超时机制数据会最终一致
        TimeUnit.SECONDS.sleep(2);
        stockInRedis = this.productService.getStock(id, ProductService.CacheUpdateStrategyActiveUpdateAndTimeout);
        productModel = this.productRepository.findById(id).orElse(null);
        Assert.assertEquals(stockInRedis, productModel.getQuantity());

        // endregion

        // 等待缓存数据超时
        TimeUnit.SECONDS.sleep(3);

        // region 缓存更新策略 - 主动更新策略+读写锁解决数据不一致问题

        executorService = Executors.newCachedThreadPool();

        concurrentThreads = 128;
        // 多条并发线程读取商品数据
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                this.productService.getStock(id, ProductService.CacheUpdateStrategyActiveUpdateAndReadWriteLock);
            });
        }

        // 1 条线程减少库存
        executorService.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                //
            }
            try {
                this.productService.decrease(id, ProductService.CacheUpdateStrategyActiveUpdateAndReadWriteLock);
            } catch (Exception e) {
                //
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        // 验证 Redis 中商品库存和数据库中商品库存一致
        stockInRedis = this.productService.getStock(id, ProductService.CacheUpdateStrategyActiveUpdateAndReadWriteLock);
        productModel = this.productRepository.findById(id).orElse(null);
        Assert.assertEquals(stockInRedis, productModel.getQuantity());

        // endregion
    }
}