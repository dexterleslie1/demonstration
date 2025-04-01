package com.future.demo;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class Tests {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Before
    public void setup() {
        this.productRepository.deleteAll();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.flushDb();
            return "ok";
        });
    }

    // 使用分布式锁，保证同一时刻只能有一个查询请求重新加载热点数据到缓存中，这样，
    // 其他的线程只需等待该线程运行完毕即可重新从Redis中获取数据或者等待一会儿后依旧无法获取锁则返回提示。
    // （在高并发热点数据情况下，线程需要等待，性能受到影响）
    @Test
    public void test1() throws InterruptedException {
        // 初始化商品数据
        ProductModel productModel = new ProductModel();
        productModel.setQuantity(10);
        productModel.setCreateTime(new Date());
        this.productRepository.save(productModel);

        Long id = this.productRepository.findAll().get(0).getId();

        String key = "cacheProductStock#" + id;

        // 把热点数据加载到缓存中，并设置过期时间
        this.redisTemplate.opsForValue().set(key, String.valueOf(productModel.getQuantity()), 1, TimeUnit.SECONDS);

        String keyLoading = "productLoading#" + id;

        AtomicInteger signal1 = new AtomicInteger();
        AtomicInteger signal2 = new AtomicInteger();

        int concurrentThreads = 128;
        int looperInner = 10000;

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < looperInner; j++) {
                    String value = this.redisTemplate.opsForValue().get(key);

                    // 热点数据过期，重新加载到缓存中
                    if (StringUtils.isEmpty(value)) {
                        RLock rLock = null;
                        try {
                            // 只允许一个线程重新加载热点数据到缓存中
                            rLock = this.redissonClient.getLock(keyLoading);
                            rLock.lock();

                            value = this.redisTemplate.opsForValue().get(key);
                            if (StringUtils.isEmpty(value)) {
                                // 重新加载热点数据线程执行
                                ProductModel productModelT = this.productRepository.findById(id).orElse(null);

                                // 模拟加载复杂的热点数据很耗时
                                TimeUnit.SECONDS.sleep(1);

                                this.redisTemplate.opsForValue().set(key, String.valueOf(productModelT.getQuantity()));

                                signal1.incrementAndGet();
                            } else {
                                // 重新加载热点数据线程退出后，其他线程获取到锁，直接命中缓存
                                signal2.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            //
                        } finally {
                            if (rLock != null) {
                                rLock.unlock();
                            }
                        }
                    } else {
                        // 缓存数据未过期，直接命中缓存
                        signal2.incrementAndGet();
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        // 只允许一个线程重新加载热点数据到缓存中
        Assert.assertEquals(1, signal1.get());
        // 缓存命中次数
        Assert.assertEquals(concurrentThreads * looperInner - 1, signal2.get());
    }

    // 设置 key 逻辑过期时间。Redis 中的 key 不设置 TTL，逻辑过期时间在 value 中设置，
    // 读取数据时判断逻辑过期时间是否过期，是则重新加载热点数据到缓存中（使用锁控制并发只能有一条线程加载热点数据到缓存，其他线程暂时返回当前过期数据），
    // 否则直接返回数据。（在高并发热点数据情况下，线程不需要等待，性能较好）
    @Test
    public void test2() throws InterruptedException {
        // 初始化商品数据
        ProductModel productModel = new ProductModel();
        productModel.setQuantity(10);
        productModel.setCreateTime(new Date());
        this.productRepository.save(productModel);

        Long id = this.productRepository.findAll().get(0).getId();

        String key = "cacheProductStock#" + id;

        // 把热点数据加载到缓存中，并设置过期时间
        this.redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(new DataWithExpiration(LocalDateTime.now().plusSeconds(1), productModel)));

        String keyLoading = "productLoading#" + id;

        AtomicInteger signal1 = new AtomicInteger();
        AtomicInteger signal2 = new AtomicInteger();

        int concurrentThreads = 128;
        int looperInner = 1000;

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < looperInner; j++) {
                    String value = this.redisTemplate.opsForValue().get(key);
                    DataWithExpiration dataWithExpiration = JSONUtil.toBean(value, DataWithExpiration.class);
                    if (LocalDateTime.now().isBefore(dataWithExpiration.getExpiration())) {
                        // 热点数据未逻辑过期直接返回
                        signal2.incrementAndGet();
                    } else {
                        // 热点数据逻辑过期，重新加载到缓存中
                        RLock rLock = null;
                        boolean acquired = false;
                        try {
                            // 只允许一个线程重新加载热点数据到缓存中
                            rLock = this.redissonClient.getLock(keyLoading);
                            acquired = rLock.tryLock();
                            if (!acquired) {
                                signal2.incrementAndGet();
                                continue;
                            }

                            // 双重检查机制，确保热点数据未逻辑过期不会被重新加载
                            value = this.redisTemplate.opsForValue().get(key);
                            dataWithExpiration = JSONUtil.toBean(value, DataWithExpiration.class);
                            if (LocalDateTime.now().isBefore(dataWithExpiration.getExpiration())) {
                                // 热点数据未逻辑过期直接返回
                                signal2.incrementAndGet();
                                continue;
                            }

                            // 重新加载热点数据线程执行
                            ProductModel productModelT = this.productRepository.findById(id).orElse(null);

                            // 模拟加载复杂的热点数据很耗时
                            TimeUnit.SECONDS.sleep(1);

                            this.redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(new DataWithExpiration(LocalDateTime.now().plusSeconds(3600), productModelT)));

                            signal1.incrementAndGet();
                        } catch (InterruptedException e) {
                            //
                        } finally {
                            if (rLock != null && acquired) {
                                rLock.unlock();
                            }
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        // 只允许一个线程重新加载热点数据到缓存中
        Assert.assertEquals(1, signal1.get());
        // 缓存命中次数
        Assert.assertEquals(concurrentThreads * looperInner - 1, signal2.get());
    }
}