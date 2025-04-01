package com.future.demo.performance;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemoryLeakService {
    private boolean stopped = true;
    private final List<String> storeList = new ArrayList<>();

    /**
     * 开始模拟memory leak逻辑
     */
    public long start() {
        long count = 0;
        this.stopped = false;
        while(!stopped) {
            String uuid = UUID.randomUUID().toString();
            storeList.add(uuid);
            count++;
        }
        return count;
    }

    /**
     * 停止模拟memory leak逻辑
     */
    public void stop() {
        this.stopped = true;
    }

    /**
     * 释放模拟memory leak占用的内存
     */
    public void releaseLeak() {
        storeList.clear();
    }
}
