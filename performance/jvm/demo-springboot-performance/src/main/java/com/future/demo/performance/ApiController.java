package com.future.demo.performance;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value="/api/v1")
@Slf4j
public class ApiController {
	@Autowired
	PasswordEncoder passwordEncoder;
    @Autowired
    private CacheManager cacheManager = null;
    private Cache cache1 = null;
    private Cache cacheMemoryHolder = null;

    ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * 这个接口支持post和get HTTP请求
	 *
	 * @param timeout
	 * @return
	 */
	@RequestMapping("sleep")
	ResponseEntity<String> timeout(@RequestParam(value = "timeout", defaultValue = "0") Long timeout) {
		if(timeout>0) {
			try {
				TimeUnit.MILLISECONDS.sleep(timeout);
			} catch (InterruptedException e) {
				//
			}
		}
		return ResponseEntity.ok("接口成功休眠" + timeout + "毫秒");
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

//    private boolean memoryConsumptionRunning = false;
//    private long memoryConsumptionCounter = 0;
//	/**
//	 * 内存消耗测试开始
//	 *
//	 * @return
//	 */
//	@GetMapping("memory/consumption/start")
//    public ObjectResponse<String> memoryConsumptionStart(){
//		memoryConsumptionRunning = true;
//		while(memoryConsumptionRunning) {
//			String uuid = UUID.randomUUID().toString();
//			Element element = new Element(uuid, uuid);
//			this.cacheMemoryHolder.put(element);
//			memoryConsumptionCounter++;
//		}
//
//		ObjectResponse<String> response = new ObjectResponse<>();
//		response.setData("成功创建 " + memoryConsumptionCounter + " 个element对象");
//        return response;
//    }
//
//	/**
//	 * 内存消耗测试结束
//	 *
//	 * @return
//	 */
//	@GetMapping("memory/consumption/stop")
//	public ObjectResponse<String> memoryConsumptionStop() {
//		memoryConsumptionRunning = false;
//
//		ObjectResponse<String> response = new ObjectResponse<>();
//		response.setData("成功停止");
//		return response;
//	}
    
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
