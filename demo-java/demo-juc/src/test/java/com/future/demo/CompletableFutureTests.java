package com.future.demo;

import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        {
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
        }
        // endregion

        // region CompletableFuture whenComplete 和 exceptionally 回调
        {
            ExecutorService executor = Executors.newCachedThreadPool();

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
        }
        // endregion

        // region 电商比价案例
        {
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
            ExecutorService executor = Executors.newCachedThreadPool();

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
        }
        // endregion

        // region 获取结果
        {
            ExecutorService executor = Executors.newCachedThreadPool();

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
        }
        // endregion

        // region 对结果处理方式
        {
            ExecutorService executor = Executors.newCachedThreadPool();

            // thenApply() 系列方法: 用于对完成的结果进行转换。它们接受一个函数作为参数，该函数将 CompletableFuture 的结果作为输入，并返回一个新的 CompletionStage，其结果是应用了该函数后的结果。thenApplyAsync() 会异步执行转换函数。
            CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenApply(v -> v + ":s2").thenApply(v -> v + ":s3");
            Assert.assertEquals("s1:s2:s3", stringCompletableFuture.get());
            // 异常发生时立刻终止执行后续的 thenApply
            stringCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenApply(v -> {
                boolean b = true;
                if (b) {
                    throw new RuntimeException(new Exception("testing"));
                }
                return v + ":s2";
            }).thenApply(v -> v + ":s3").exceptionally(e -> null);
            Assert.assertNull(stringCompletableFuture.get());

            // handle() 方法：和 thenApply() 方法类似，但当 `CompletableFuture` 完成时，无论正常还是异常，都执行一个双函数（BiFunction），它接受结果和异常（如果有的话），并返回一个新的结果。
            stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
                boolean b = true;
                if (b) {
                    throw new RuntimeException(new Exception("testing"));
                }
                return "s1";
            }, executor).handle((v, e) -> v + ":s2").handle((v, e) -> v + ":s3").exceptionally(e -> null);
            Assert.assertEquals("null:s2:s3", stringCompletableFuture.get());

            // **thenAccept**：当 `CompletableFuture` 完成时，执行一个接受结果的消费者（Consumer）。如果 `CompletableFuture` 异常完成，则回调不会被调用。
            List<String> signalList = new ArrayList<>();
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenAccept(signalList::add);
            voidCompletableFuture.get();
            Assert.assertEquals(1, signalList.size());
            Assert.assertEquals("s1", signalList.get(0));

            // **thenRun**：当 `CompletableFuture` 完成时，执行一个无参数的 Runnable。它不关心结果。
            signalList = new ArrayList<>();
            List<String> finalSignalList = signalList;
            voidCompletableFuture = CompletableFuture.supplyAsync(() -> "s1", executor).thenRun(() -> {
                finalSignalList.add("step1");
            });
            voidCompletableFuture.get();
            Assert.assertEquals(1, signalList.size());
            Assert.assertEquals("step1", signalList.get(0));

            executor.shutdown();
        }
        // endregion

        // region applyToEither `applyToEither()` 方法用于两个 CompletableFuture 中，任何一个先完成就执行指定的函数，并返回一个新的 CompletableFuture。它不关心哪个 CompletableFuture 先完成，只关心第一个完成的结果。
        {
            ExecutorService executor = Executors.newCachedThreadPool();

            CompletableFuture<String> player1 = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignored) {

                }
                return "Player1";
            }, executor);

            CompletableFuture<String> player2 = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ignored) {

                }
                return "Player2";
            }, executor);

            CompletableFuture<String> completableFuture4 = player1.applyToEither(player2, v -> v + " 胜出");
            // Player1 比 Player2 快，所以 Player1 胜出
            Assert.assertEquals("Player1 胜出", completableFuture4.get());

            executor.shutdown();
        }
        // endregion

        // region thenCombine：`thenCombine()` 方法用于将两个 CompletableFuture 的结果合并成一个新的 CompletableFuture。它会在两个 CompletableFuture 都完成后，将它们的结果作为参数传递给指定的 BiFunction，并将 BiFunction 的返回值作为新的 CompletableFuture 的结果。
        {
            ExecutorService executor = Executors.newCachedThreadPool();

            CompletableFuture<List<Integer>> completableFuture51 = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ignored) {

                }
                return new ArrayList<Integer>() {{
                    this.add(1);
                    this.add(2);
                }};
            }, executor);
            CompletableFuture<List<Integer>> completableFuture52 = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {

                }
                return new ArrayList<Integer>() {{
                    this.add(3);
                    this.add(4);
                }};
            }, executor);
            CompletableFuture<List<Integer>> completableFuture53 = completableFuture51.thenCombine(completableFuture52, (a, b) -> {
                a.addAll(b);
                return a;
            });
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), completableFuture53.get());

            executor.shutdown();
        }
        // endregion

        // region CompletableFuture 异常处理
        {

            ExecutorService executor = Executors.newCachedThreadPool();

            // region 测试CompletableFuture#get()

            // 测试 IllegalArgumentException
            CompletableFuture completableFuture5 = CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("IllegalArgumentException");
            }, executor);
            try {
                try {
                    completableFuture5.get();
                } catch (ExecutionException ex) {
                    // CompletableFuture#get() 执行异常时只抛出 ExecutionException，里面包装了业务异常，通过 getCause() 获取
                    Throwable cause = ex.getCause();
                    if (cause instanceof RuntimeException) {
                        // 如果是 RuntimeException
                        if (cause.getCause() == null) {
                            // RuntimeException 没有 cause，则直接抛出
                            throw (RuntimeException) cause;
                        } else {
                            // 否则抛出 RuntimeException 中的 cause
                            cause = cause.getCause();
                            throw cause;
                        }
                    }
                }
                Assert.fail();
            } catch (Throwable ex) {
                Assert.assertTrue(ex instanceof IllegalArgumentException);
            }

            // 测试 BusinessException
            completableFuture5 = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException(new BusinessException("H"));
            }, executor);
            try {
                try {
                    completableFuture5.get();
                } catch (ExecutionException ex) {
                    // CompletableFuture#get() 执行异常时只抛出 ExecutionException，里面包装了业务异常，通过 getCause() 获取
                    Throwable cause = ex.getCause();
                    if (cause instanceof RuntimeException) {
                        // 如果是 RuntimeException
                        if (cause.getCause() == null) {
                            // RuntimeException 没有 cause，则直接抛出
                            throw (RuntimeException) cause;
                        } else {
                            // 否则抛出 RuntimeException 中的 cause
                            cause = cause.getCause();
                            throw cause;
                        }
                    }
                }
                Assert.fail();
            } catch (Throwable ex) {
                Assert.assertTrue(ex instanceof BusinessException);
                Assert.assertEquals("H", ((BusinessException) ex).getErrorMessage());
            }

            // 测试 RuntimeException
            completableFuture5 = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("A");
            }, executor);
            try {
                try {
                    completableFuture5.get();
                } catch (ExecutionException ex) {
                    // CompletableFuture#get() 执行异常时只抛出 ExecutionException，里面包装了业务异常，通过 getCause() 获取
                    Throwable cause = ex.getCause();
                    if (cause instanceof RuntimeException) {
                        // 如果是 RuntimeException
                        if (cause.getCause() == null) {
                            // RuntimeException 没有 cause，则直接抛出
                            throw (RuntimeException) cause;
                        } else {
                            // 否则抛出 RuntimeException 中的 cause
                            cause = cause.getCause();
                            throw cause;
                        }
                    }
                }
                Assert.fail();
            } catch (Throwable ex) {
                Assert.assertTrue(ex instanceof RuntimeException);
                Assert.assertEquals("A", ((RuntimeException) ex).getMessage());
            }

            // endregion

            // region 测试 CompletableFuture#join()

            // 测试 IllegalArgumentException
            completableFuture5 = CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("IllegalArgumentException");
            }, executor);
            try {
                try {
                    completableFuture5.join();
                } catch (CompletionException ex) {
                    // CompletableFuture#join() 执行异常时只抛出 CompletionException，里面包装了业务异常，通过 getCause() 获取
                    Throwable cause = ex.getCause();
                    if (cause instanceof RuntimeException) {
                        // 如果是 RuntimeException
                        if (cause.getCause() == null) {
                            // RuntimeException 没有 cause，则直接抛出
                            throw (RuntimeException) cause;
                        } else {
                            // 否则抛出 RuntimeException 中的 cause
                            cause = cause.getCause();
                            throw cause;
                        }
                    }
                }
                Assert.fail();
            } catch (Throwable ex) {
                Assert.assertTrue(ex instanceof IllegalArgumentException);
            }

            // 测试 BusinessException
            completableFuture5 = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException(new BusinessException("H"));
            }, executor);
            try {
                try {
                    completableFuture5.join();
                } catch (CompletionException ex) {
                    // CompletableFuture#join() 执行异常时只抛出 CompletionException，里面包装了业务异常，通过 getCause() 获取
                    Throwable cause = ex.getCause();
                    if (cause instanceof RuntimeException) {
                        // 如果是 RuntimeException
                        if (cause.getCause() == null) {
                            // RuntimeException 没有 cause，则直接抛出
                            throw (RuntimeException) cause;
                        } else {
                            // 否则抛出 RuntimeException 中的 cause
                            cause = cause.getCause();
                            throw cause;
                        }
                    }
                }
                Assert.fail();
            } catch (Throwable ex) {
                Assert.assertTrue(ex instanceof BusinessException);
                Assert.assertEquals("H", ((BusinessException) ex).getErrorMessage());
            }

            // 测试 RuntimeException
            completableFuture5 = CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("A");
            }, executor);
            try {
                try {
                    completableFuture5.join();
                } catch (CompletionException ex) {
                    // CompletableFuture#join() 执行异常时只抛出 CompletionException，里面包装了业务异常，通过 getCause() 获取
                    Throwable cause = ex.getCause();
                    if (cause instanceof RuntimeException) {
                        // 如果是 RuntimeException
                        if (cause.getCause() == null) {
                            // RuntimeException 没有 cause，则直接抛出
                            throw (RuntimeException) cause;
                        } else {
                            // 否则抛出 RuntimeException 中的 cause
                            cause = cause.getCause();
                            throw cause;
                        }
                    }
                }
                Assert.fail();
            } catch (Throwable ex) {
                Assert.assertTrue(ex instanceof RuntimeException);
                Assert.assertEquals("A", ((RuntimeException) ex).getMessage());
            }

            // endregion

            executor.shutdown();
        }

        // endregion
    }
}
