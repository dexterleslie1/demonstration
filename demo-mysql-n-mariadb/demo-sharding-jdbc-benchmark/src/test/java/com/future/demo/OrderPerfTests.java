package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.bean.Order;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
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
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
@Slf4j
public class OrderPerfTests {

    final static int SecondsWaitForDockerContainerToStopOrStart = 15;
    final static String StrConstOrder = "order";

    @Param(value = {"100w"/*, "10w", "100w", "200w", "300w", "400w", "500w", "1kw", "2kw", "3kw", "5kw", "10kw"*/})
    String springProfile;
    @Param(value = {"512m"/*, "2g"*/})
    String databaseMemory;

    OrderMapper orderMapper;
    SnowflakeService snowflakeService;

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
                .jvmArgs("-Xmx4G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() throws IOException, InterruptedException {
        // region 启动当前 Spring profile 对应的数据库，其他 profile 的关闭

        String command = "docker compose stop";
        //接收正常结果流
        ByteArrayOutputStream susStream = new ByteArrayOutputStream();
        //接收异常结果流
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor exec = DefaultExecutor.builder().get();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        int code = exec.execute(commandLine);
        Assertions.assertEquals(0, code, errStream.toString(StandardCharsets.UTF_8.name()));

        Properties properties = new Properties();
        properties.load(new ClassPathResource("application.properties").getInputStream());
        String dataSourceNames = properties.getProperty("spring.shardingsphere.datasource.names");
        String[] dataSourceNamesSplit = dataSourceNames.split(",");
        for (int i = 1; i <= dataSourceNamesSplit.length; i++) {
            command = "docker compose up -d db-" + springProfile + "-" + i;
            //接收正常结果流
            susStream = new ByteArrayOutputStream();
            //接收异常结果流
            errStream = new ByteArrayOutputStream();
            commandLine = CommandLine.parse(command);
            exec = DefaultExecutor.builder().get();
            streamHandler = new PumpStreamHandler(susStream, errStream);
            exec.setStreamHandler(streamHandler);
            code = exec.execute(commandLine, new HashMap<String, String>() {{
                this.put("innodbBufferPoolSize", databaseMemory);
            }});
            Assertions.assertEquals(0, code, errStream.toString(StandardCharsets.UTF_8.name()));
        }

        command = "docker compose up -d demo-zookeeper";
        //接收正常结果流
        susStream = new ByteArrayOutputStream();
        //接收异常结果流
        errStream = new ByteArrayOutputStream();
        commandLine = CommandLine.parse(command);
        exec = DefaultExecutor.builder().get();
        streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        code = exec.execute(commandLine);
        Assertions.assertEquals(0, code, errStream.toString(StandardCharsets.UTF_8.name()));

        TimeUnit.SECONDS.sleep(SecondsWaitForDockerContainerToStopOrStart);

        // endregion

        //容器获取
        // https://stackoverflow.com/questions/31267274/spring-boot-programmatically-setting-profiles
        SpringApplication application = new SpringApplication(Application.class);
        application.setAdditionalProfiles(springProfile);
        context = application.run();

        //获取对象
        orderMapper = context.getBean(OrderMapper.class);
        snowflakeService = context.getBean(SnowflakeService.class);

        int totalCount = Integer.parseInt(Objects.requireNonNull(context.getEnvironment().getProperty("totalCount")));
        int totalCountInDB = this.orderMapper.count();
        // 如果当前数据库记录总数不等于预期记录数，则初始化
        if (totalCount != totalCountInDB) {
            log.info("databaseMemory {} springProfile {} 当前数据库记录总数 {} 不等于 预期记录数 {} 需要重新初始化数据", databaseMemory, springProfile, totalCountInDB, totalCount);

            this.orderMapper.truncate();

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
                    Long snowflakeId = this.snowflakeService.getId(StrConstOrder).getId();
                    OrderRandomlyUtil.injectUserIdGenIntoOrderId(snowflakeId, order.getUserId(), order);
                    orderList.add(order);

                    if (orderList.size() == 1000) {
                        this.orderMapper.addBatch(orderList);
                        orderList = new ArrayList<>();
                    }
                }
                if (!orderList.isEmpty()) {
                    this.orderMapper.addBatch(orderList);
                }
            }, threadPool)).collect(Collectors.toList()).toArray(new CompletableFuture[]{})).join();
            threadPool.shutdown();
        } else {
            log.info("databaseMemory {} springProfile {} 当前数据库记录总数等于预期记录数 {}", databaseMemory, springProfile, totalCount);
        }

        totalCountInDB = this.orderMapper.count();
        Assertions.assertEquals(totalCount, totalCountInDB);

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

//    /**
//     * 用户查询指定日期范围+所有状态的订单
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testListByUserIdAndWithoutStatus(Blackhole blackhole) {
//        // 用户查询指定日期范围+所有状态的订单
//        Long userId = userIdArray[RandomUtil.randomInt(0, userIdArray.length)];
//        // 日期范围随机开始时间点
//        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
//        LocalDateTime endTime = startTime.plusMonths(1);
//        List<Order> orderList = this.orderMapper.listByUserId(userId, null, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
//        blackhole.consume(orderList);
//    }
//
//    /**
//     * 用户查询指定日期范围+指定状态的订单
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testListByUserIdAndStatus(Blackhole blackhole) {
//        // 用户查询指定日期范围+指定状态的订单
//        Long userId = userIdArray[RandomUtil.randomInt(0, userIdArray.length)];
//        // 日期范围随机开始时间点
//        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
//        LocalDateTime endTime = startTime.plusMonths(1);
//        Status status = OrderRandomlyUtil.getStatusRandomly();
//        List<Order> orderList = this.orderMapper.listByUserId(userId, status, DeleteStatus.Normal, startTime, endTime, 0L, 20L);
//        blackhole.consume(orderList);
//    }
//
//    /**
//     * 商家查询指定日期范围+所有状态的订单
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testListByMerchantIdAndWithoutStatus(Blackhole blackhole) {
//        // 商家查询指定日期范围+所有状态的订单
//        Long merchantId = merchantIdArray[RandomUtil.randomInt(0, merchantIdArray.length)];
//        // 日期范围随机开始时间点
//        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
//        LocalDateTime endTime = startTime.plusMonths(1);
//        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
//        List<Order> orderList = this.orderMapper.listByMerchantId(merchantId, null, deleteStatus, startTime, endTime, 0L, 20L);
//        blackhole.consume(orderList);
//    }
//
//    /**
//     * 商家查询指定日期范围+指定状态的订单
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testListByMerchantIdAndStatus(Blackhole blackhole) {
//        // 商家查询指定日期范围+指定状态的订单
//        Long merchantId = merchantIdArray[RandomUtil.randomInt(0, merchantIdArray.length)];
//        // 日期范围随机开始时间点
//        LocalDateTime startTime = OrderRandomlyUtil.getCreateTimeRandomly();
//        LocalDateTime endTime = startTime.plusMonths(1);
//        Status status = OrderRandomlyUtil.getStatusRandomly();
//        DeleteStatus deleteStatus = OrderRandomlyUtil.getDeleteStatusRandomly();
//        List<Order> orderList = this.orderMapper.listByMerchantId(merchantId, status, deleteStatus, startTime, endTime, 0L, 20L);
//        blackhole.consume(orderList);
//    }
}
