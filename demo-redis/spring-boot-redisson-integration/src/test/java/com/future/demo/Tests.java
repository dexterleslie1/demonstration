package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
@Slf4j
public class Tests {
	@Autowired
	RedissonClient redissonClient;

	@Test
	public void test() {
		String key = UUID.randomUUID().toString();

		RLock rLock = this.redissonClient.getLock(key);
		boolean acquired = false;
		try {
			acquired = rLock.tryLock();
		} finally {
			if(acquired) {
				try {
					rLock.unlock();
				} catch (Exception ignored) {

				}
			}
		}

		Assert.assertTrue(acquired);
	}
}