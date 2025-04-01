package com.future.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MyLock {
    private static DefaultRedisScript<Void> UnlockScript = null;

    static {
        UnlockScript = new DefaultRedisScript<>();
        UnlockScript.setResultType(Void.class);
        UnlockScript.setLocation(new ClassPathResource("unlock.lua"));
    }

    StringRedisTemplate redisTemplate;

    private String lockUuid = UUID.randomUUID().toString();
    private String key;

    public MyLock(String key, StringRedisTemplate redisTemplate) {
        this.key = key;
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param leaseTimeInSeconds 锁自动释放时间，单位秒
     * @return
     */
    public boolean lock(int leaseTimeInSeconds) {
        // 保存锁的 uuid 为键值是为了在释放锁时判断 Redis 中的 key 是否属于本锁
        Boolean b = this.redisTemplate.opsForValue().setIfAbsent(key, lockUuid, leaseTimeInSeconds, TimeUnit.SECONDS);
        return b != null && b;
    }

    /**
     * 用于演示释放锁存在线程安全问题
     *
     * @param stuckInSecondsBeforeDeleteKey 删除 key 前卡住的时间，单位秒
     */
    public void unlockWithThreadSafeProblem(int stuckInSecondsBeforeDeleteKey) {
        // 判断 Redis 中的 key 是否属于本锁，是则删除该 key，否则不操作
        String value = this.redisTemplate.opsForValue().get(key);
        if (lockUuid.equals(value)) {
            // 注意：如果线程在这里卡住一段时间（可能 JVM GC 停顿导致卡顿），到线程恢复后有可能会释放其他线程持有的锁，所以需要把判断锁标识和删除操作放在一个原子操作中。
            if (stuckInSecondsBeforeDeleteKey >= 0) {
                try {
                    TimeUnit.SECONDS.sleep(stuckInSecondsBeforeDeleteKey);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.redisTemplate.delete(key);
        }
    }

    public void unlock() {
        this.redisTemplate.execute(UnlockScript, Collections.singletonList(key), lockUuid);
    }
}
