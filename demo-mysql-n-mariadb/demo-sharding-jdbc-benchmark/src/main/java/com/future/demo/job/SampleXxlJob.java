package com.future.demo.job;

import com.future.demo.bean.Order;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.OrderRandomlyUtil;
import com.tencent.devops.leaf.service.SnowflakeService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class SampleXxlJob {
    final static String StrConstOrder = "order";
    final static String StrConstPath = "/my-path1";

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CuratorFramework curatorFramework;
    @Autowired
    SnowflakeService snowflakeService;

    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {
        String jobParam = XxlJobHelper.getJobParam();
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}, 任务参数 = {}", shardIndex, shardTotal, jobParam);
        log.info("分片参数：当前分片序号 = {}, 总分片数 = {}, 任务参数 = {}", shardIndex, shardTotal, jobParam);

        DistributedDoubleBarrier barrier = null;
        try {
            // 插入数据记录总数
            int totalCount = 10000 * 100 / shardTotal;
            if (!StringUtils.isEmpty(jobParam)) {
                int intTemporary = Integer.parseInt(jobParam);
                if (intTemporary < 10000) {
                    intTemporary = 10000;
                }
                totalCount = intTemporary / shardTotal;
            }

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

            barrier = new DistributedDoubleBarrier(curatorFramework, StrConstPath, shardTotal);
            barrier.enter(1, TimeUnit.MINUTES);

            log.info("当前分片序号 = {}, 总分片数 = {} 开始执行 ...", shardIndex, shardTotal);

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
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (barrier != null) {
                barrier.leave(1, TimeUnit.MINUTES);

                int totalCountInDB = orderMapper.count();
                log.info("数据库中总记录数 {}", totalCountInDB);
            }
        }

    }
}
