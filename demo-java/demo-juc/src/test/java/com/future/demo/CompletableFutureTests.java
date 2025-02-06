package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class CompletableFutureTests {
    @Test
    public void test() throws ExecutionException, InterruptedException {
        // region 使用 CompletableFuture.runAsync 和 CompletableFuture.supplyAsync 创建 CompletableFuture

        ExecutorService executor = Executors.newCachedThreadPool();

        // 没有返回值的 CompletableFuture
        AtomicInteger counter = new AtomicInteger();
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                counter.incrementAndGet();
            } catch (InterruptedException ignored) {

            }
        }, executor);
        Assert.assertNull(completableFuture.get());
        Assert.assertEquals(1, counter.get());

        // 有返回值 CompletableFuture
        AtomicInteger counter1 = new AtomicInteger();
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                counter1.incrementAndGet();
            } catch (InterruptedException ignored) {

            }
            return "Hello world!";
        }, executor);
        Assert.assertEquals("Hello world!", completableFuture1.get());
        Assert.assertEquals(1, counter1.get());

        executor.shutdown();

        // endregion

        // region CompletableFuture whenComplete 和 exceptionally 回调

        executor = Executors.newCachedThreadPool();

        // CompletableFuture whenComplete 业务方法处理完毕回调
        AtomicInteger counter2 = new AtomicInteger();
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }
            return "Hello world!";
        }, executor).whenComplete((data, exception) -> {
            counter2.incrementAndGet();
        });
        Assert.assertEquals("Hello world!", completableFuture2.get());
        Assert.assertEquals(1, counter2.get());

        // CompletableFuture exceptionally 异常处理回调
        AtomicInteger counter3 = new AtomicInteger();
        AtomicInteger counter4 = new AtomicInteger();
        CompletableFuture<String> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }

            boolean b = true;
            if (b) {
                throw new RuntimeException(new Exception("testing"));
            }

            return "Hello world!";
        }, executor).whenComplete((data, exception) -> {
            counter3.incrementAndGet();
        }).exceptionally(exception -> {
            counter4.incrementAndGet();
            return null;
        });
        Assert.assertNull(completableFuture3.get());
        Assert.assertEquals(1, counter3.get());
        Assert.assertEquals(1, counter4.get());

        executor.shutdown();

        // endregion

        // region 电商比价案例

        List<NetMall> netMallList = Arrays.asList(
                NetMall.builder().name("jd").price(BigDecimal.valueOf(101.11)).build(),
                NetMall.builder().name("tmall").price(BigDecimal.valueOf(91.21)).build(),
                NetMall.builder().name("taobao").price(BigDecimal.valueOf(121.00)).build(),
                NetMall.builder().name("pingduoduo").price(BigDecimal.valueOf(87.32)).build()

        );

        // 不并发
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<String> stringList = netMallList.stream().map(o -> String.format("%s 价格为 %f", o.getName(), o.getPrice())).collect(Collectors.toList());
        log.debug(String.join(",", stringList));

        stopWatch.stop();
        // 不使用并发耗时较长
        Assert.assertTrue(String.valueOf(stopWatch.getTotalTimeMillis()), stopWatch.getTotalTimeMillis() >= 4000 && stopWatch.getTotalTimeMillis() <= 4100);

        // 使用 CompletableFuture 并发
        executor = Executors.newCachedThreadPool();

        stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService finalExecutor = executor;
        stringList = netMallList.stream().map(o -> CompletableFuture.supplyAsync(() -> String.format("%s 价格为 %f", o.getName(), o.getPrice()), finalExecutor)).collect(Collectors.toList())
                .stream().map(CompletableFuture::join).collect(Collectors.toList());
        log.debug(String.join(",", stringList));

        stopWatch.stop();
        // 使用 CompletableFuture 并发耗时为最长的任务时间
        Assert.assertTrue(String.valueOf(stopWatch.getTotalTimeMillis()), stopWatch.getTotalTimeMillis() >= 1000 && stopWatch.getTotalTimeMillis() <= 1100);

        executor.shutdown();

        // endregion

        // region 获取结果

        executor = Executors.newCachedThreadPool();

        // get() 方法：等待直到有返回值
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            return "Hello world!";
        }, executor);
        Assert.assertEquals("Hello world!", stringCompletableFuture.get());

        // get(long timeout, TimeUnit unit) 方法：等待超时抛出 TimeoutException
        stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            return "Hello world!";
        }, executor);
        String result = null;
        try {
            result = stringCompletableFuture.get(500, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (TimeoutException ignored) {
            Assert.assertNull(result);
        }

        // join() 方法：与 `get()` 类似，但更简洁，因为它直接抛出原始异常，而不是包装在 `ExecutionException` 中。
        stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            return "Hello world!";
        }, executor);
        Assert.assertEquals("Hello world!", stringCompletableFuture.join());

        // getNow(T valueIfAbsent) 方法：不需要等待结果，可以接受使用默认值。  如果异步操作可能需要很长时间才能完成，这对于快速响应或避免阻塞非常有用。
        stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {

            }
            return "Hello world!";
        }, executor);
        Assert.assertEquals("Absent", stringCompletableFuture.getNow("Absent"));
        TimeUnit.MILLISECONDS.sleep(1050);
        Assert.assertEquals("Hello world!", stringCompletableFuture.getNow("Absent"));

        // get()+complete(T value) 方法：complete(T value) 用于手动完成 CompletableFuture，并将结果传递给它。让 get() 等待中断
        stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) {

            }
            return "Hello world!";
        }, executor);
        CompletableFuture<String> finalStringCompletableFuture = stringCompletableFuture;
        executor.submit(() -> {
            // 提前中断 CompletableFuture.get() 等待
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException ignored) {

            }
            finalStringCompletableFuture.complete("complete signal");
        });
        Assert.assertEquals("complete signal", stringCompletableFuture.get());

        executor.shutdown();

        // endregion
    }
}
