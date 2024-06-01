package com.future.demo.pkg2;

public class RedisCacheService implements CacheService {

    @Override
    public String getType() {
        return "redis缓存";
    }

}
