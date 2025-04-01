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
	@Autowired
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
	public void testAnnotationCachable() throws JsonProcessingException {
		ProductModel productModel = new ProductModel();
		productModel.setQuantity(10);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);
		productModel = new ProductModel();
		productModel.setQuantity(11);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);

		Long id1 = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(0).getId();
		Long id2 = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(1).getId();

		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id1));
		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id2));

		this.productService.testCachable(id1);
		this.productService.testCachable(id2);

		String JSON = this.redisTemplate.opsForValue().get("productCache::" + id1);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(JSON);
		Assert.assertEquals(10, jsonNode.get("quantity").asInt());
		JSON = this.redisTemplate.opsForValue().get("productCache::" + id2);
		jsonNode = objectMapper.readTree(JSON);
		Assert.assertEquals(11, jsonNode.get("quantity").asInt());
	}

	@Test
	public void testAnnotationCacheEvict() throws JsonProcessingException {
		ProductModel productModel = new ProductModel();
		productModel.setQuantity(10);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);
		productModel = new ProductModel();
		productModel.setQuantity(11);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);

		Long id1 = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(0).getId();
		Long id2 = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(1).getId();

		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id1));
		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id2));

		this.productService.testCachable(id1);
		this.productService.testCachable(id2);

		this.productService.testCacheEvict(id1);
		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id1));
		this.productService.testCacheEvict(id2);
		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id2));

		this.productService.testCachable(id1);
		this.productService.testCachable(id2);

		String JSON = this.redisTemplate.opsForValue().get("productCache::" + id1);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(JSON);
		Assert.assertEquals(10, jsonNode.get("quantity").asInt());
		JSON = this.redisTemplate.opsForValue().get("productCache::" + id2);
		jsonNode = objectMapper.readTree(JSON);
		Assert.assertEquals(11, jsonNode.get("quantity").asInt());
	}

	@Test
	public void testAnnotationCachePut() throws Exception {
		ProductModel productModel = new ProductModel();
		productModel.setQuantity(10);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);
		productModel = new ProductModel();
		productModel.setQuantity(11);
		productModel.setCreateTime(new Date());
		this.productRepository.save(productModel);

		Long id1 = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(0).getId();
		Long id2 = this.productRepository.findAll(Sort.by(Sort.Order.asc("id"))).get(1).getId();

		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id1));
		Assert.assertNull(this.redisTemplate.opsForValue().get("productCache::" + id2));

		this.productService.testCachePut(id1);
		this.productService.testCachePut(id2);

		String JSON = this.redisTemplate.opsForValue().get("productCache::" + id1);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(JSON);
		Assert.assertEquals(9, jsonNode.get("quantity").asInt());
		JSON = this.redisTemplate.opsForValue().get("productCache::" + id2);
		jsonNode = objectMapper.readTree(JSON);
		Assert.assertEquals(10, jsonNode.get("quantity").asInt());
	}
}