package com.future.demo.util;

import com.future.demo.bean.Order;
import com.future.demo.mapper.OrderMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchInsertionUtil {
    /**
     * @param totalCount        插入数据记录总数
     * @param orderMapper
     * @param sqlSessionFactory
     * @param log
     */
    public static void batchInsert(int totalCount, OrderMapper orderMapper, SqlSessionFactory sqlSessionFactory, Logger log) {
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

            /*List<Order> orderList = new ArrayList<>();
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
            }*/

            SqlSession sqlSession = null;
            try {
                sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
                OrderMapper orderMapperInternal = sqlSession.getMapper(OrderMapper.class);

                int count = 0;
                for (int i = 0; i < internalRunLoopCount; i++) {
                    Order order = orderRandomlyUtil.createRandomly();
                    orderMapperInternal.add(order);
                    count++;

                    if (count == 1000) {
                        sqlSession.commit();
                        count = 0;
                    }
                }
                if (count > 0) {
                    sqlSession.commit();
                    count = 0;
                }
            } catch (Exception ex) {
                if (sqlSession != null) {
                    sqlSession.rollback();
                }
                throw ex;
            } finally {
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }
        }, threadPool)).collect(Collectors.toList()).toArray(new CompletableFuture[]{})).join();
        threadPool.shutdown();

        stopWatch.stop();
        log.info("totalCount={} 耗时 {} 毫秒", totalCount, stopWatch.getTotalTimeMillis());

        int totalCountInDB = orderMapper.count();
        Assertions.assertEquals(totalCount, totalCountInDB);
    }
}
