package com.future.demo;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
@Slf4j
public class Tests {
	@Autowired
	ProductRepository productRepository;
	@Resource(name = "stringRedisTemplate")
	StringRedisTemplate redisTemplate;
	@Autowired
	RedissonClient redissonClient;

	@Before
	public void setup() {
		this.productRepository.deleteAll();
		redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return "ok";
			}
		});
	}

	// 使用分布式锁，保证同一时刻只能有一个查询请求重新加载热点数据到缓存中，这样，其他的线程只需等待该线程运行完毕，即可重新从Redis中获取数据。
	@Test
	public void test1() throws InterruptedException {
		ProductModel productModel = new ProductModel();
		productModel.setQuantity(10);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);

		Long id = this.productRepository.findAll().get(0).getId();
		String key = "cacheProductStock#" + id;
		String keyLoading = "productLoading#" + id;

		AtomicInteger signal1 = new AtomicInteger();
		AtomicInteger signal2 = new AtomicInteger();
		AtomicInteger signal3 = new AtomicInteger();

		int concurrentThreads = 50;
		int looperInner = 100;

		ExecutorService executorService = Executors.newCachedThreadPool();
		for(int i=0; i<concurrentThreads; i++) {
			executorService.submit(() -> {
				for(int j=0; j<looperInner; j++) {
					String value = this.redisTemplate.opsForValue().get(key);
					if (StringUtils.isBlank(value)) {
						RLock rLock = this.redissonClient.getLock(keyLoading);
						boolean acquired = false;
						try {
							acquired = rLock.tryLock();
							if(acquired) {
								ProductModel productModelT = this.productRepository.findById(id).orElse(null);
								if (productModelT == null) {
									// 缓存空对象
									this.redisTemplate.opsForValue().set(key, "0");
									signal1.incrementAndGet();
								} else {
									this.redisTemplate.opsForValue().set(key, String.valueOf(productModel.getQuantity()));
									signal3.incrementAndGet();
								}
							}
						} finally {
							if(acquired) {
								try {
									rLock.unlock();
								} catch (Exception ignored) {

								}
							} else {
								signal2.incrementAndGet();
							}
						}
					} else{
						signal2.incrementAndGet();
					}
				}
			});
		}

		executorService.shutdown();
		while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

		Assert.assertEquals(signal1.get() + "", 0, signal1.get());
		Assert.assertEquals(signal2.get() + "", concurrentThreads * looperInner - 1, signal2.get());
		Assert.assertEquals(signal3.get() + "", 1, signal3.get());
	}
}