//package com.future.demo;
//
//import java.util.UUID;
//
///**
// * @author dexterleslie@gmail.com
// */
//public class RedissonLockLoadTester {
//    public void setup() {
//
//    }
//
//    public void doLock() {
//        String uuid = UUID.randomUUID().toString();
//        try {
//            RedissonManager.tryLock(uuid);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            RedissonManager.unlock(uuid);
//        }
//    }
//
//    public void teardown() {
//    }
//}
