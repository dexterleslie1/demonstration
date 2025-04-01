package com.future.demo;

import java.util.concurrent.TimeUnit;

/**
 * 用于辅助测试 synchronized 特性
 */
public class SynchronizedTestingAssistantObject {
    public synchronized void instanceMethod1WithSynchronized(String checkPoint) {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ignored) {

        }

        MyCheckPoint.check(checkPoint);
    }

    public synchronized void instanceMethod2WithSynchronized(String checkPoint) {
        MyCheckPoint.check(checkPoint);
    }

    public void instanceMethod3WithoutSynchronized(String checkPoint) {
        MyCheckPoint.check(checkPoint);
    }

    public static synchronized void staticMethod1WithSynchronized(String checkPoint) {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ignored) {

        }

        MyCheckPoint.check(checkPoint);
    }

    public static synchronized void staticMethod2WithSynchronized(String checkPoint) {
        MyCheckPoint.check(checkPoint);
    }
}
