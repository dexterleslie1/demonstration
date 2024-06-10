package com.future.demo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
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

    public void store(String token, CustomizeUser user) {
        Element element = new Element(token, user);
        this.cacheTokenToUser.put(element);
    }

    public CustomizeUser get(String token) {
        Element element = this.cacheTokenToUser.get(token);
        if (element == null) {
            return null;
        }

        return (CustomizeUser) element.getObjectValue();
    }

    public void remove(String token) {
        if (!StringUtils.isBlank(token))
            this.cacheTokenToUser.remove(token);
    }
}
