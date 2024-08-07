package com.future.demo.executor.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 *
 */
public class ExecutorServiceTests {
    private final static Logger logger = LoggerFactory.getLogger(ExecutorServiceTests.class);

    private final static Random Random = new Random();

    /**
     *
     */
    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<10; i++) {
            final int iVar = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int randomInt = Random.nextInt(500);
                    if(randomInt<=0) {
                        randomInt = 10;
                    }
                    try {
                        Thread.sleep(randomInt);
                    } catch (InterruptedException e) {
                        //
                    }
                    logger.info("Current sequence: {}", iVar);
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(500, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testSingleThreadScheduledExecutor() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        for(int i=0; i<100; i++) {
            int finalI = i;
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    logger.debug("Worker-" + finalI);
                }
            }, 5, 5, TimeUnit.SECONDS);
        }

        System.out.println();

        executorService.shutdown();
        while(!executorService.awaitTermination(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void test1() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(6);
        for(int i=0; i<100; i++) {
            int finalI = i;
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Thread currentThread = Thread.currentThread();
                    logger.debug("Current Thread {} execute Worker-{}", currentThread, finalI);
                }
            }, 5, 5, TimeUnit.SECONDS);
        }

        System.out.println();

        executorService.shutdown();
        while(!executorService.awaitTermination(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testExceptionHandling() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future> futureList = new ArrayList<>();
        for(int i=0; i<5; i++) {
            int finalI = i;
            futureList.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if(finalI == 3) {
                        throw new RuntimeException("8888888888");
                    }

                    Thread currentThread = Thread.currentThread();
                    logger.debug("Current Thread {} execute Worker-{}", currentThread, finalI);
                }
            }));
        }

        try {
            for(Future future : futureList) {
                future.get();
                logger.debug("+++++++++++++++++++辅助日志，查看当前时间");
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    public void testExecuteMethod() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<5; i++) {
            int finalI = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Thread currentThread = Thread.currentThread();
                    logger.debug("Current Thread {} execute Worker-{}", currentThread, finalI);
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(100, TimeUnit.MILLISECONDS));

        executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("888888");
            }
        });
        executorService.shutdown();
        while(!executorService.awaitTermination(100, TimeUnit.MILLISECONDS));
    }
}
