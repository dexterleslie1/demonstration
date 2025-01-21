package com.future.demo.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    @Autowired
    private CacheManager cacheManager = null;

    private Cache cacheTest = null;
    private Cache cacheMemoryFootprint = null;

    @PostConstruct
    public void init() {
        this.cacheTest = this.cacheManager.getCache("cacheTest");
        this.cacheMemoryFootprint = this.cacheManager.getCache("cacheMemoryFootprint");
    }

    @GetMapping("test1")
    public ResponseEntity<String> test1() {
        String uuid = UUID.randomUUID().toString();
        this.cacheTest.put(new Element(uuid, uuid));
        return ResponseEntity.ok("调用成功");
    }

    private boolean runLoopStop = true;
    /**
     * 用于开始测试ehcache内存占用情况
     *
     * @return
     */
    @GetMapping("memory/footprint/start")
    public ResponseEntity<String> memoryFootprintStart() {
        this.runLoopStop = false;
        while(!this.runLoopStop) {
            String uuid = UUID.randomUUID().toString();
            Element element = new Element(uuid, uuid);
            element.setTimeToLive(15);
            this.cacheMemoryFootprint.put(element);
        }
        return ResponseEntity.ok("调用成功");
    }

    /**
     * 用于停止测试ehcache内存占用情况
     *
     * @return
     */
    @GetMapping("memory/footprint/stop")
    public ResponseEntity<String> memoryFootprintStop() {
        this.runLoopStop = true;
        return ResponseEntity.ok("调用成功");
    }
}
