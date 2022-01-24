package com.future.demo.performance;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.yyd.common.http.response.ObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author dexterleslie@gmail.com
 */
@RestController
@RequestMapping(value="/api/v1")
public class ApiController {
	@Autowired
	PasswordEncoder passwordEncoder;
    @Autowired
    private CacheManager cacheManager = null;
    private Cache cache1 = null;
    private Cache cacheMemoryHolder = null;

    ExecutorService executorService = Executors.newCachedThreadPool();

	@PostMapping("timeout")
	ResponseEntity<String> timeout(@RequestParam(value = "timeout", defaultValue = "0") Integer timeout) {
//		if(timeout>0) {
//			try {
//				TimeUnit.MILLISECONDS.sleep(timeout);
//			} catch (InterruptedException e) {
//				//
//			}
//		}
		String uuid = UUID.randomUUID().toString();
//		String passwordEncoded = this.passwordEncoder.encode(uuid);
//		return ResponseEntity.ok("成功调用timeout接口，passwordEncoded=" + passwordEncoded);
		return ResponseEntity.ok("成功调用timeout接口，uuid=" + uuid);
	}

	@PostMapping("timeoutWithThreadPool")
	ResponseEntity<String> timeoutWithThreadPool(@RequestParam(value = "timeout", defaultValue = "0") Integer timeout) {
		if(timeout>0) {
			executorService.submit(() -> {
				try {
					TimeUnit.MILLISECONDS.sleep(timeout);
				} catch (InterruptedException e) {
					//
				}
			});
		}
		return ResponseEntity.ok("成功调用timeoutWithThreadPool接口");
	}
    
    /**
     * 
     */
    @PostConstruct
    public void init() {
    	this.cache1 = this.cacheManager.getCache("cache1");
    	this.cacheMemoryHolder = this.cacheManager.getCache("cacheMemoryHolder");
    }

    @PutMapping("memory/consumption")
    public ObjectResponse<String> memoryConsumption(@RequestParam(value = "memoryInMb", defaultValue = "0") int memoryInMb,
													@RequestParam(value = "memoryTimeToLiveInSeconds", defaultValue = "0") int memoryTimeToLiveInSeconds){
    	Assert.isTrue(memoryInMb>0, "内存消耗值不能小于等于0");
    	Assert.isTrue(memoryTimeToLiveInSeconds>0, "内存超时时间不能小于等于0");

    	String key = UUID.randomUUID().toString();
    	int memoryInBytes = memoryInMb * 1024 * 1024;
        byte []bytes = new byte[memoryInBytes];
        Random random = new Random();
        random.nextBytes(bytes);
        Element element = new Element(key, bytes);
        element.setTimeToLive(memoryTimeToLiveInSeconds);
        this.cacheMemoryHolder.put(element);
        List<String> keys = this.cacheMemoryHolder.getKeys();
        long totalConsumeMemoryInBytes = 0;
        for(String keyTemporary : keys) {
        	element = this.cacheMemoryHolder.get(keyTemporary);
        	if(element!=null) {
				totalConsumeMemoryInBytes += ((byte[]) element.getObjectValue()).length;
			}
		}

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功分配内存 " + memoryInMb + "MB，总消耗内存：" + totalConsumeMemoryInBytes/(1024*1024) + "MB");
        return response;
    }
    
    /**
     * 
     * @return
     */
    @RequestMapping(value="cpu/loaded", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> cpuLoaded(){
    	Random random = new Random();
    	int x = random.nextInt(Integer.MAX_VALUE);
    	int y = random.nextInt(Integer.MAX_VALUE);
    	int z = random.nextInt(Integer.MAX_VALUE);
    	long result = (x-z) * y;
    	String uuid = UUID.randomUUID().toString();
    	Element element = new Element(uuid, result);
    	int seconds = random.nextInt(300);
    	if(seconds<=0) {
    		seconds = 1;
    	}
    	element.setTimeToLive(seconds);
    	this.cache1.put(element);
        return ResponseEntity.ok(String.valueOf(result));
    }
    
    /**
     * 
     * @return
     * @throws InterruptedException 
     */
    @RequestMapping(value="thread/loaded", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> threadLoaded() throws InterruptedException{
    	Random random = new Random();
    	int x = random.nextInt(Integer.MAX_VALUE);
    	int y = random.nextInt(Integer.MAX_VALUE);
    	int z = random.nextInt(Integer.MAX_VALUE);
    	long result = (x-z) * y;
    	String uuid = UUID.randomUUID().toString();
    	Element element = new Element(uuid, result);
    	int seconds = random.nextInt(300);
    	if(seconds<=0) {
    		seconds = 1;
    	}
    	element.setTimeToLive(seconds);
    	this.cache1.put(element);
    	int millis = random.nextInt(5000);
    	if(millis<=0) {
    		millis = 1;
    	}
    	Thread.sleep(millis);
        return ResponseEntity.ok(String.valueOf(result));
    }
}
