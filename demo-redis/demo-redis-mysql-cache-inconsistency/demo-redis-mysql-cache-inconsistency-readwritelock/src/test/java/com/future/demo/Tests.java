package com.future.demo;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
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
		redisTemplate.execute(new RedisCallback() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return "ok";
			}
		});
	}

	@Test
	public void test() throws InterruptedException {
		ProductModel productModel = new ProductModel();
		productModel.setQuantity(10);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);

		Long id = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(0).getId();

		ExecutorService executorService = Executors.newCachedThreadPool();

		int concurrentThreads = 50;
		int looperInner = 10000;
		for(int i=0; i<concurrentThreads; i++) {
			executorService.submit(() -> {
				for(int j=0; j<looperInner; j++) {
					try {
						this.productService.getStock(id);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		for(int i=0; i<concurrentThreads; i++) {
			executorService.submit(() -> {
				try {
					this.productService.decrease(id);
				} catch (Exception e) {
					//
				}
			});
		}

		executorService.shutdown();
		while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

		int stockInRedis = this.productService.getStock(id);
		productModel = this.productRepository.findById(id).orElse(null);
//		System.out.println(stockInRedis);
//		System.out.println(productModel.getQuantity());
		Assert.assertEquals(0, stockInRedis);
		Assert.assertEquals(0, productModel.getQuantity());
	}
}