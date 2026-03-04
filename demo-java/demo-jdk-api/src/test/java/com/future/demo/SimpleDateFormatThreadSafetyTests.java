package com.future.demo;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * 演示 SimpleDateFormat 不是线程安全的测试类
 * 
 * SimpleDateFormat 不是线程安全的原因：
 * 1. SimpleDateFormat 内部使用 Calendar 对象来存储日期信息
 * 2. 在多线程环境下，多个线程共享同一个 SimpleDateFormat 实例时，
 *    会同时操作同一个 Calendar 对象，导致数据混乱、解析错误或抛出异常
 * 
 * 解决方案：
 * 1. 每次使用时创建新的 SimpleDateFormat 实例（性能较差）
 * 2. 使用 ThreadLocal 为每个线程创建独立的 SimpleDateFormat 实例（推荐）
 * 3. 使用 Java 8+ 的 DateTimeFormatter（线程安全，推荐）
 */
public class SimpleDateFormatThreadSafetyTests {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final int THREAD_COUNT = 10;
    private static final int TASK_COUNT_PER_THREAD = 100;

    /**
     * 测试 SimpleDateFormat 在多线程环境下的线程安全问题
     * 这个测试可能会抛出异常或产生错误的解析结果
     */
    @Test
    public void testSimpleDateFormatNotThreadSafe() throws InterruptedException {
        System.out.println("=== 测试 SimpleDateFormat 线程安全问题 ===");
        
        // 共享的 SimpleDateFormat 实例（线程不安全）
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        
        // 用于存储解析结果的集合
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());
        
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT * TASK_COUNT_PER_THREAD);
        
        // 提交任务
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                for (int j = 0; j < TASK_COUNT_PER_THREAD; j++) {
                    try {
                        // 生成测试日期字符串
                        String dateStr = String.format("2024-01-%02d 12:00:%02d", 
                                (j % 28) + 1, j % 60);
                        
                        // 解析日期
                        Date date = sdf.parse(dateStr);
                        
                        // 格式化日期
                        String formatted = sdf.format(date);
                        
                        // 验证结果是否正确
                        if (!dateStr.equals(formatted)) {
                            errors.add(String.format("线程%d-任务%d: 原始=%s, 解析后=%s", 
                                    threadIndex, j, dateStr, formatted));
                        } else {
                            results.add(formatted);
                        }
                    } catch (ParseException e) {
                        errors.add(String.format("线程%d-任务%d: ParseException - %s", 
                                threadIndex, j, e.getMessage()));
                    } catch (Exception e) {
                        errors.add(String.format("线程%d-任务%d: Exception - %s", 
                                threadIndex, j, e.getClass().getSimpleName() + ": " + e.getMessage()));
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        
        // 等待所有任务完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        // 输出结果
        System.out.println("成功解析的数量: " + results.size());
        System.out.println("错误数量: " + errors.size());
        
        if (!errors.isEmpty()) {
            System.out.println("\n前10个错误示例:");
            errors.stream().limit(10).forEach(System.out::println);
        }
        
        // SimpleDateFormat 不是线程安全的，所以这里可能会有错误
        // 注意：由于线程调度的不确定性，不是每次运行都会出现错误
        // 但多运行几次或增加线程数/任务数，出现错误的概率会大大增加
        System.out.println("\n注意：SimpleDateFormat 不是线程安全的！");
        System.out.println("如果看到错误，说明确实存在线程安全问题。");
        System.out.println("如果没有错误，可以增加线程数或任务数，或多次运行测试。");
    }

    /**
     * 测试使用 ThreadLocal 包装 SimpleDateFormat 的线程安全方案
     */
    @Test
    public void testSimpleDateFormatWithThreadLocal() throws InterruptedException {
        System.out.println("\n=== 测试 ThreadLocal 方案（线程安全） ===");
        
        // 使用 ThreadLocal 为每个线程创建独立的 SimpleDateFormat 实例
        ThreadLocal<SimpleDateFormat> threadLocalSdf = ThreadLocal.withInitial(
                () -> new SimpleDateFormat(DATE_PATTERN)
        );
        
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT * TASK_COUNT_PER_THREAD);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                // 获取当前线程的 SimpleDateFormat 实例
                SimpleDateFormat sdf = threadLocalSdf.get();
                
                for (int j = 0; j < TASK_COUNT_PER_THREAD; j++) {
                    try {
                        String dateStr = String.format("2024-01-%02d 12:00:%02d", 
                                (j % 28) + 1, j % 60);
                        
                        Date date = sdf.parse(dateStr);
                        String formatted = sdf.format(date);
                        
                        if (!dateStr.equals(formatted)) {
                            errors.add(String.format("线程%d-任务%d: 原始=%s, 解析后=%s", 
                                    threadIndex, j, dateStr, formatted));
                        } else {
                            results.add(formatted);
                        }
                    } catch (Exception e) {
                        errors.add(String.format("线程%d-任务%d: %s", 
                                threadIndex, j, e.getMessage()));
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        System.out.println("成功解析的数量: " + results.size());
        System.out.println("错误数量: " + errors.size());
        
        if (!errors.isEmpty()) {
            System.out.println("\n错误示例:");
            errors.stream().limit(5).forEach(System.out::println);
        } else {
            System.out.println("\n✓ ThreadLocal 方案线程安全，没有错误！");
        }
    }

    /**
     * 测试使用 Java 8+ DateTimeFormatter 的线程安全方案（推荐）
     */
    @Test
    public void testDateTimeFormatterThreadSafe() throws InterruptedException {
        System.out.println("\n=== 测试 DateTimeFormatter（线程安全，推荐） ===");
        
        // DateTimeFormatter 是线程安全的，可以安全地共享
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT * TASK_COUNT_PER_THREAD);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                for (int j = 0; j < TASK_COUNT_PER_THREAD; j++) {
                    try {
                        String dateStr = String.format("2024-01-%02d 12:00:%02d", 
                                (j % 28) + 1, j % 60);
                        
                        // 解析
                        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
                        
                        // 格式化
                        String formatted = dateTime.format(formatter);
                        
                        if (!dateStr.equals(formatted)) {
                            errors.add(String.format("线程%d-任务%d: 原始=%s, 解析后=%s", 
                                    threadIndex, j, dateStr, formatted));
                        } else {
                            results.add(formatted);
                        }
                    } catch (Exception e) {
                        errors.add(String.format("线程%d-任务%d: %s", 
                                threadIndex, j, e.getMessage()));
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        System.out.println("成功解析的数量: " + results.size());
        System.out.println("错误数量: " + errors.size());
        
        if (!errors.isEmpty()) {
            System.out.println("\n错误示例:");
            errors.stream().limit(5).forEach(System.out::println);
        } else {
            System.out.println("\n✓ DateTimeFormatter 线程安全，没有错误！");
        }
    }

    /**
     * 对比测试：展示三种方案的区别
     */
    @Test
    public void testComparison() {
        System.out.println("\n=== 方案对比 ===");
        System.out.println("1. SimpleDateFormat（共享实例）- 线程不安全 ❌");
        System.out.println("   - 多线程共享同一个实例会导致数据混乱");
        System.out.println("   - 可能抛出 ParseException 或产生错误结果");
        System.out.println();
        
        System.out.println("2. SimpleDateFormat + ThreadLocal - 线程安全 ✓");
        System.out.println("   - 每个线程有独立的 SimpleDateFormat 实例");
        System.out.println("   - 适合 Java 8 之前的项目");
        System.out.println("   - 需要手动管理 ThreadLocal，避免内存泄漏");
        System.out.println();
        
        System.out.println("3. DateTimeFormatter - 线程安全 ✓（推荐）");
        System.out.println("   - Java 8+ 引入，线程安全");
        System.out.println("   - 可以安全地共享实例");
        System.out.println("   - API 更现代，功能更强大");
        System.out.println("   - 推荐在新项目中使用");
    }
}
