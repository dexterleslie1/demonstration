package com.future.demo;

import com.future.demo.bean.Order;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class OrderTests {

    /**
     * 等待容器准备好秒数
     */
    final static int SecondsToWaitForContainer = 10;

    ApplicationContext applicationContext;

    @AfterEach
    public void aftereach() {
        if (this.applicationContext != null) {
            ((ConfigurableApplicationContext) this.applicationContext).close();
        }
    }

    /**
     * @param totalCount                    插入数据记录总数
     * @param databaseMemory                数据库内存大小
     * @param availableProcessorsMultiplier 并发线程数为CPU数的倍数
     * @throws InterruptedException
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("parameterizedTestParametersSupplier")
    public void test(int totalCount, String databaseMemory, int availableProcessorsMultiplier) throws InterruptedException, IOException {
        // region 准备数据库容器

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
        Assertions.assertEquals(0, code, errStream.toString(StandardCharsets.UTF_8));

        command = "docker compose up -d";
        //接收正常结果流
        susStream = new ByteArrayOutputStream();
        //接收异常结果流
        errStream = new ByteArrayOutputStream();
        commandLine = CommandLine.parse(command);
        exec = DefaultExecutor.builder().get();
        streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        code = exec.execute(commandLine, new HashMap<>() {{
            this.put("innodbBufferPoolSize", databaseMemory);
        }});
        Assertions.assertEquals(0, code, errStream.toString(StandardCharsets.UTF_8));

        TimeUnit.SECONDS.sleep(SecondsToWaitForContainer);

        // endregion

        // region 初始化 SpringBoot

        applicationContext = SpringApplication.run(Application.class);
        OrderMapper orderMapper = applicationContext.getBean(OrderMapper.class);

        // endregion

        orderMapper.truncate();

        int availableProcessors = Runtime.getRuntime().availableProcessors() * availableProcessorsMultiplier;
        long remainder = totalCount % availableProcessors;
        int concurrentThreads = availableProcessors;
        if (remainder != 0) {
            concurrentThreads = concurrentThreads + 1;
        }
        long runLoopCount = (totalCount - remainder) / availableProcessors;

        ExecutorService threadPool = Executors.newCachedThreadPool();
        OrderRandomlyUtil orderRandomlyUtil = new OrderRandomlyUtil(totalCount);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

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
                    orderMapper.addBatch(orderList);
                    orderList = new ArrayList<>();
                }
            }
            if (!orderList.isEmpty()) {
                orderMapper.addBatch(orderList);
            }
        }, threadPool)).collect(Collectors.toList()).toArray(CompletableFuture[]::new)).join();
        threadPool.shutdown();

        stopWatch.stop();
        log.info("totalCount={},databaseMemory={},availableProcessorsMultiplier={} 耗时 {} 毫秒", totalCount, databaseMemory, availableProcessorsMultiplier, stopWatch.getTotalTimeMillis());

        int totalCountInDB = orderMapper.count();
        Assertions.assertEquals(totalCount, totalCountInDB);
    }

    static Stream<Arguments> parameterizedTestParametersSupplier() {
        return Stream.of(
//                Arguments.of(10000 * 100, "512m", 1),
//                Arguments.of(10000 * 100, "512m", 2),
//                Arguments.of(10000 * 100, "512m", 3),
//                Arguments.of(10000 * 100, "512m", 4),
//                Arguments.of(10000 * 100, "512m", 5),
//                Arguments.of(10000 * 100, "512m", 6),
                Arguments.of(10000 * 100, "2g", 1),
                Arguments.of(10000 * 100, "2g", 2),
                Arguments.of(10000 * 100, "2g", 3),
                Arguments.of(10000 * 100, "2g", 4),
                Arguments.of(10000 * 100, "2g", 5),
                Arguments.of(10000 * 100, "2g", 6)
        );
    }
}
