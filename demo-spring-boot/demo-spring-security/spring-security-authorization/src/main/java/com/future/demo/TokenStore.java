package com.future.demo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TokenStore {
    @Autowired
    CacheManager cacheManager;
    Cache cacheTokenToUser;

    @PostConstruct
    public void init1() {
        this.cacheTokenToUser = this.cacheManager.getCache("cacheTokenToUser");
    }

    public void store(String token, MyUser user) {
        Element element = new Element(token, user);
        this.cacheTokenToUser.put(element);
    }

    public MyUser get(String token) {
        Element element = this.cacheTokenToUser.get(token);
        if(element==null) {
            return null;
        }

        return (MyUser)element.getObjectValue();
    }

    public void remove(String token) {
        this.cacheTokenToUser.remove(token);
    }
}
