package com.future.demo.quartz;

import java.util.Date;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     *
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void cronbTask() {
        Date date = new Date();
        System.out.println("cronbTask " + date);
    }

    // 每分钟执行一次
    @Scheduled(cron = "0 * * * * ?")
    public void doCronbEveryMinute() {
        Date date = new Date();
        System.out.println("doCronbEveryMinute " + date);
    }

    // 每秒钟执行一次
    @Scheduled(cron = "0/1 * * * * ?")
    public void doCronbEverySecond() {
        Date date = new Date();
        System.out.println("doCronbEverySecond " + date);
    }

    @Scheduled(cron = "0 0/2 10 * * ?")
    public void doCronbStartAt10Every2Minute() {
        Date date = new Date();
        System.out.println("doCronbStartAt10Every2Minute " + date);
    }
}
