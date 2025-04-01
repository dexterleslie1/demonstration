package com.future.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 
 * @author dexterleslie@gmail.com
 *
 */
@Component
public class Task1 {
	/**
	 *
	 */
	@Scheduled(initialDelay = 0, fixedDelay = 60000)
    public void initialTask() {
        Date date = new Date();
        System.out.println("InitialTask " + date);
    }

    private boolean hasRun = false;
    @Scheduled(initialDelay = 0, fixedDelay = 1000)
    public void onlyOnceTask() {
        // 保证任务只执行一次
        synchronized (this) {
            if (hasRun) {
                return;
            }
            hasRun = true;
        }
        Date date = new Date();
        System.out.println("onlyOnceTask " + date);
    }

    // 每隔5秒触发一次，0/5 表示从0秒开始
    @Scheduled(cron = "0/5 * * * * ?")
    public void cronbTask() {
        Date date = new Date();
        System.out.println("每5秒触发一次：" + date);
    }

    // 每分钟触发一次
    @Scheduled(cron = "0 0/1 * * * ?")
    public void doCronbEveryMinute() {
        Date date = new Date();
        System.out.println("每分钟触发一次：" + date);
    }

    // 每秒触发一次
    @Scheduled(cron = "0/1 * * * * ?")
    public void doCronbEverySecond() {
        Date date = new Date();
        System.out.println("每秒触发一次：" + date);
    }

    // 每小时的每分钟的第1秒触发一次
    @Scheduled(cron = "1 * * * * ?")
    public void triggerOnceAtFirstSecondEveryMinute() {
        Date date = new Date();
        System.out.println("每小时的每分钟的第1秒触发一次：" + date);
    }

    // 每天13:06:03触发一次
    @Scheduled(cron = "3 6 13 * * ?")
    public void triggerTest1() {
        Date date = new Date();
        System.out.println("每天13:06:03触发一次：" + date);
    }

    // 每天13点每2分钟触发
    @Scheduled(cron = "0 0/2 13 * * ?")
    public void doCronbStartAt10Every2Minute() {
        Date date = new Date();
        System.out.println("每天13点每2分钟触发：" + date);
    }
}
