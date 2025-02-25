package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    CuratorFramework curatorFramework;

    @Test
    public void test() throws Exception {
        // region 测试 barrier
        // https://www.cnblogs.com/LiZhiW/p/4937547.html

        // 手动模式
        {
            ExecutorService threadPool = Executors.newCachedThreadPool();

            String uuid = "/" + UUID.randomUUID();

            DistributedBarrier controllerBarrier = new DistributedBarrier(curatorFramework, uuid);
            // 主控设置栅栏
            controllerBarrier.setBarrier();

            for (int i = 0; i < 5; i++) {
                threadPool.submit(() -> {
                    DistributedBarrier distributedBarrier = new DistributedBarrier(curatorFramework, uuid);
                    try {
                        log.info("线程 {} 等待 ...", Thread.currentThread().getName());
                        // 等到移除栅栏才继续执行
                        distributedBarrier.waitOnBarrier();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    log.info("线程 {} 结束等待", Thread.currentThread().getName());
                });
            }

            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 5));

            // 主控移除栅栏
            controllerBarrier.removeBarrier();

            threadPool.shutdown();
            while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        }

        // 自动模式
        {
            ExecutorService threadPool = Executors.newCachedThreadPool();

            String uuid = "/" + UUID.randomUUID();

            for (int i = 0; i < 5; i++) {
                threadPool.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(RandomUtils.nextInt(0, 6));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(curatorFramework, uuid, 5);
                    try {
                        // 等到 memberQty=5 才继续执行
                        distributedDoubleBarrier.enter();

                        log.info("线程 {} 继续执行", Thread.currentThread().getName());

                        try {
                            TimeUnit.SECONDS.sleep(RandomUtils.nextInt(0, 6));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            // 等到 memberQty=5 才继续执行
                            distributedDoubleBarrier.leave();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            log.info("线程 {} 结束执行", Thread.currentThread().getName());
                        }
                    }
                });
            }

            threadPool.shutdown();
            while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        }

        // endregion
    }

    /**
     * 测试 DistributedDoubleBarrier enter 后没有 leave 是否依旧正常
     */
    @Test
    public void testDistributedDoubleBarrierEnterWithoutLeaving() throws InterruptedException {
        String path = "/my-path";
        int memberQty = 2;

        ExecutorService threadPool = Executors.newCachedThreadPool();

        for (int i = 0; i < memberQty; i++) {
            threadPool.submit(() -> {
                log.info("线程 {} 开始", Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(0, 6));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(curatorFramework, path, memberQty);
                try {
                    // 等到 memberQty=2 才继续执行
                    distributedDoubleBarrier.enter();

                    log.info("线程 {} 继续执行", Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 注意：必须要调用 leave 方法，否则同一个 path 下的 DistributedDoubleBarrier 在后续调用栅栏效果会不生效
                    /*try {
                        // 等到 memberQty=2 才继续执行
                        distributedDoubleBarrier.leave();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        log.info("线程 {} 结束执行", Thread.currentThread().getName());
                    }*/
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        log.info("-------------------------- 第二次测试 --------------------------");
        threadPool = Executors.newCachedThreadPool();

        for (int i = 0; i < memberQty; i++) {
            threadPool.submit(() -> {
                log.info("线程 {} 开始", Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(0, 6));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(curatorFramework, path, memberQty);
                try {
                    // 等到 memberQty=2 才继续执行
                    distributedDoubleBarrier.enter();

                    log.info("线程 {} 继续执行", Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 注意：必须要调用 leave 方法，否则同一个 path 下的 DistributedDoubleBarrier 在后续调用栅栏效果会不生效
                    /*try {
                        // 等到 memberQty=2 才继续执行
                        distributedDoubleBarrier.leave();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        log.info("线程 {} 结束执行", Thread.currentThread().getName());
                    }*/
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
    }
}
