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

    @PostConstruct
    public void init() {
        this.cacheTest = this.cacheManager.getCache("cacheTest");
    }

    @GetMapping("test1")
    public ResponseEntity<String> test1() {
        String uuid = UUID.randomUUID().toString();
        this.cacheTest.put(new Element(uuid, uuid));
        return ResponseEntity.ok("调用成功");
    }
}
