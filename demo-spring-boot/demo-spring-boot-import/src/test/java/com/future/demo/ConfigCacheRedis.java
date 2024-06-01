package com.future.demo;

import com.future.demo.pkg2.CacheType;
import com.future.demo.pkg2.EnableMyCache;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMyCache(type = CacheType.Redis)
public class ConfigCacheRedis {
}
