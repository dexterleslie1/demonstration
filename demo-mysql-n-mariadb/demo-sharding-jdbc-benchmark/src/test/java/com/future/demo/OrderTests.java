package com.future.demo;

import com.future.demo.bean.Order;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
public class OrderTests {

    final static String StrConstOrder = "order";

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    SnowflakeService snowflakeService;

    @Test
    public void test() {
        // 插入数据记录总数
        int totalCount = 10000 * 100;

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
                order.setId(this.snowflakeService.getId(StrConstOrder).getId());
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

        stopWatch.stop();
        log.info("totalCount={} 耗时 {} 毫秒", totalCount, stopWatch.getTotalTimeMillis());

        int totalCountInDB = orderMapper.count();
        Assertions.assertEquals(totalCount, totalCountInDB);
    }
}
