package com.future.demo.mapdb;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class ConfigMapdb {
    @Bean(destroyMethod = "close")
    public DB db() {
        // 堆内内存模式；也可用 memoryDirectDB() 使用堆外内存，或 fileDB(...) 持久化到磁盘
        // 会OOM
        /*return DBMaker.memoryDB().make();*/
        // 不会OOM
        return DBMaker.fileDB("mapdb.db").make();
        // 使用启动参数-XX:MaxDirectMemorySize=8g不会OOM
        /*return DBMaker.memoryDirectDB().make();*/
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService mapdbExpireExecutor() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "mapdb-expire");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService mapdbCompactExecutor() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "mapdb-compact");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean
    public ConcurrentMap<String, String> mapTest(DB db) {
        return db.hashMap("mapTest")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();
    }

    @Bean
    public ConcurrentMap<String, String> mapMemoryFootprint(DB db,
                                                            ScheduledExecutorService mapdbExpireExecutor) {
        return db.hashMap("mapMemoryFootprint")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                /*// 写入后 15 秒过期
                .expireAfterCreate(15, TimeUnit.SECONDS)
                // MapDB 需要后台线程才会真正驱逐过期条目
                .expireExecutor(mapdbExpireExecutor)
                .expireExecutorPeriod(1000)
                // 空闲空间达到 20% 时自动 compact，回收 mapdb.db 磁盘空间
                .expireCompactThreshold(0.2)*/
                .createOrOpen();
    }
}
