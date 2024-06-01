package com.future.demo.pkg2;

public class LocalCacheService implements CacheService {

    @Override
    public String getType() {
        return "本地缓存";
    }

}
