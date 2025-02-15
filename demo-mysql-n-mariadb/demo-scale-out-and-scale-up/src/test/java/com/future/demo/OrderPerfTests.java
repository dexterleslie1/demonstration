package com.future.demo;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.DeleteStatus;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import com.future.demo.mapper.OrderMapper;
import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 15, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
public class OrderPerfTests {

    @Param(value = {"50000000"/*"10000", "100000", "1000000", "10000000", "20000000", "30000000"*/})
    private long totalCount = 0;

    OrderMapper orderMapper;

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(OrderPerfTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() {
        //容器获取
        context = SpringApplication.run(Application.class);
        //获取对象
        orderMapper = context.getBean(OrderMapper.class);

        this.orderMapper.truncate();

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long remainder = totalCount % availableProcessors;
        int concurrentThreads = availableProcessors;
        if (remainder != 0) {
            concurrentThreads = concurrentThreads + 1;
        }
        long runLoopCount = (totalCount - remainder) / availableProcessors;

        ExecutorService threadPool = Executors.newCachedThreadPool();
        int finalConcurrentThreads = concurrentThreads;
        CompletableFuture.allOf(IntStream.range(0, concurrentThreads).mapToObj(index -> CompletableFuture.runAsync(() -> {
            long internalRunLoopCount = runLoopCount;
            if (remainder != 0 && index + 1 == finalConcurrentThreads) {
                internalRunLoopCount = remainder;
            }

            List<Order> orderList = new ArrayList<>();
            for (int i = 0; i < internalRunLoopCount; i++) {
                Order order = new Order();
                order.setId(IdUtil.getSnowflake().nextId());
                order.setCreateTime(LocalDateTime.now());
                order.setUserId(RandomUtil.randomLong());
                order.setMerchantId(RandomUtil.randomLong());
                order.setTotalAmount(new BigDecimal(1000));
                order.setTotalCount(10);
                order.setStatus(Status.Unpay);
                order.setDeleteStatus(DeleteStatus.Normal);
                orderList.add(order);

                if (orderList.size() == 1000) {
                    this.orderMapper.addBatch(orderList);
                    orderList = new ArrayList<>();
                }
            }
            if (!orderList.isEmpty()) {
                this.orderMapper.addBatch(orderList);
            }
        }, threadPool)).collect(Collectors.toList()).toArray(CompletableFuture[]::new)).join();
        threadPool.shutdown();

        Assertions.assertEquals(totalCount, this.orderMapper.count());
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

    @Benchmark
    public void test(Blackhole blackhole) {
        Long userId = RandomUtil.randomLong();
        List<Order> orderList = this.orderMapper.findByUserId(userId);
        blackhole.consume(orderList);
    }
}
