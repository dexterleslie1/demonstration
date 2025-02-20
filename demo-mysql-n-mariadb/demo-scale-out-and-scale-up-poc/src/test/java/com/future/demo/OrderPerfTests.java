package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.DeleteStatus;
import com.future.demo.bean.Order;
import com.future.demo.bean.Status;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
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
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
public class OrderPerfTests {

    @Param(value = {"10000", "100000", "1000000", "2000000", "3000000", "4000000", "5000000", "10000000", "20000000", "30000000", "50000000", "100000000"})
    private long totalCountParam = 0;

    OrderMapper orderMapper;

    long[] userIdArray = null;
    long[] orderIdArray = null;
    long[] merchantIdArray = null;

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
                .jvmArgs("-Xmx20G", "-server")
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

        // 数据库总记录数大于当前totalCountParam，则删除之前的数据
        int totalCountInDB = this.orderMapper.count();
        if (totalCountInDB > totalCountParam) {
            this.orderMapper.truncate();
        }

        totalCountInDB = this.orderMapper.count();
        int totalCount = (int) totalCountParam - totalCountInDB;

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long remainder = totalCount % availableProcessors;
        int concurrentThreads = availableProcessors;
        if (remainder != 0) {
            concurrentThreads = concurrentThreads + 1;
        }
        long runLoopCount = (totalCount - remainder) / availableProcessors;

        ExecutorService threadPool = Executors.newCachedThreadPool();
        OrderRandomlyUtil orderRandomlyUtil = new OrderRandomlyUtil(totalCount);
        int finalConcurrentThreads = concurrentThreads;
        CompletableFuture.allOf(IntStream.range(0, concurrentThreads).mapToObj(index -> CompletableFuture.runAsync(() -> {
            long internalRunLoopCount = runLoopCount;
            if (remainder != 0 && index + 1 == finalConcurrentThreads) {
                internalRunLoopCount = remainder;
            }

            List<Order> orderList = new ArrayList<>();
            for (int i = 0; i < internalRunLoopCount; i++) {
                Order order = orderRandomlyUtil.createRandomly();
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

        Assertions.assertEquals(totalCountParam, this.orderMapper.count());

        // 加载所有用户ID
        userIdArray = this.orderMapper.listUserIdAll().stream().mapToLong(o -> o).toArray();
        // 加载所有商家ID
        merchantIdArray = this.orderMapper.listMerchantIdAll().stream().mapToLong(o -> o).toArray();
        // 加载所有订单ID
        orderIdArray = this.orderMapper.listIdAll().stream().mapToLong(o -> o).toArray();
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

//    @Benchmark
//    public void testInsertion(Blackhole blackhole) {
//        Order order = OrderUtil.createRandomly();
//        this.orderMapper.add(order);
//    }

    /**
     * 根据订单ID查询
     *
     * @param blackhole
     */
    @Benchmark
    public void testGetById(Blackhole blackhole) {
        // 根据订单ID查询
        Long orderId = orderIdArray[RandomUtil.randomInt(0, orderIdArray.length)];
        Order order = this.orderMapper.get(orderId);
        blackhole.consume(order);
    }

    /**
     * 用户查询指定日期范围+所有状态的订单
     *
     * @param blackhole
     */
    @Benchmark
    public void testListByUserIdAndWithoutStatus(Blackhole blackhole) {
        // 用户查询指定日期范围+所有状态的订单
        Long userId = userIdArray[RandomUtil.randomInt(0, userIdArray.length)];
        // 日期范围随机开始时间点
        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = startTime.plusMonths(1);
        List<Order> orderList = this.orderMapper.listByUserId(userId, null, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
        blackhole.consume(orderList);
    }

    /**
     * 用户查询指定日期范围+指定状态的订单
     *
     * @param blackhole
     */
    @Benchmark
    public void testListByUserIdAndStatus(Blackhole blackhole) {
        // 用户查询指定日期范围+指定状态的订单
        Long userId = userIdArray[RandomUtil.randomInt(0, userIdArray.length)];
        // 日期范围随机开始时间点
        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = startTime.plusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        List<Order> orderList = this.orderMapper.listByUserId(userId, status, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
        blackhole.consume(orderList);
    }

    /**
     * 商家查询指定日期范围+所有状态的订单
     *
     * @param blackhole
     */
    @Benchmark
    public void testListByMerchantIdAndWithoutStatus(Blackhole blackhole) {
        // 商家查询指定日期范围+所有状态的订单
        Long merchantId = merchantIdArray[RandomUtil.randomInt(0, merchantIdArray.length)];
        // 日期范围随机开始时间点
        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = startTime.plusMonths(1);
        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
        List<Order> orderList = this.orderMapper.listByMerchantId(merchantId, null, deleteStatus, startTime, endTime, 0L, 20L);
        blackhole.consume(orderList);
    }

    /**
     * 商家查询指定日期范围+指定状态的订单
     *
     * @param blackhole
     */
    @Benchmark
    public void testListByMerchantIdAndStatus(Blackhole blackhole) {
        // 商家查询指定日期范围+指定状态的订单
        Long merchantId = merchantIdArray[RandomUtil.randomInt(0, merchantIdArray.length)];
        // 日期范围随机开始时间点
        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
        LocalDateTime endTime = startTime.plusMonths(1);
        Status status = OrderRandomlyUtil.getStatusRandomly();
        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
        List<Order> orderList = this.orderMapper.listByMerchantId(merchantId, status, deleteStatus, startTime, endTime, 0L, 20L);
        blackhole.consume(orderList);
    }
}
