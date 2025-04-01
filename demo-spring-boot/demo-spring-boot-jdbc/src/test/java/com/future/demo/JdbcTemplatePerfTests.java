package com.future.demo;

import com.future.common.exception.BusinessException;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Warmup(iterations = 4, time = 5, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(-1)
public class JdbcTemplatePerfTests {

    Random random = new Random();
    OperationLogService operationLogService;
    JdbcTemplate jdbcTemplate;

    List<Long> userIdList = new ArrayList<>();

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(JdbcTemplatePerfTests.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup
    public void setup() {
        //容器获取
        context = SpringApplication.run(Application.class);
        //获取对象
        this.operationLogService = context.getBean(OperationLogService.class);
        this.jdbcTemplate = context.getBean(JdbcTemplate.class);

        for (long i = 1; i <= 200000; i++) {
            userIdList.add(i);
        }
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

    //        @Benchmark
    public void testAdd() {
        int randomIndex = random.nextInt(userIdList.size());
        Long userId = userIdList.get(randomIndex);
        Long operatorId = userId;
        Long passiveId = userId;
        randomIndex = random.nextInt(OperationType.values().length);
        OperationType operationType = OperationType.values()[randomIndex];
        int randomLength = random.nextInt(2048);
        String content = RandomStringUtils.randomAlphanumeric(randomLength);
        this.operationLogService.add(userId, operatorId, passiveId, operationType, content);
    }

    @Benchmark
    public void testList() throws BusinessException {
        int randomIndex = random.nextInt(userIdList.size());
        Long userId = userIdList.get(randomIndex);
        int randomPage = random.nextInt(100);
        int randomSize = random.nextInt(100);
        if (randomSize <= 0)
            randomSize = 1;

        List<OperationType> operationTypeList = this.getRandomOperationTypeList();
        this.operationLogService.list(userId, operationTypeList, randomPage, randomSize);
    }

    //        @Benchmark
    public void testConnectAndDisconnect() throws SQLException {
        Connection connection = null;
        try {
            connection = this.jdbcTemplate.getDataSource().getConnection();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

    }

    List<OperationType> getRandomOperationTypeList() {
        List<OperationType> operationTypeList = null;
        int randomLength = random.nextInt(OperationType.values().length);
        if (randomLength > 0) {
            operationTypeList = new ArrayList<>();
            for (int i = 0; i < randomLength; i++) {
                int randomIndex = random.nextInt(OperationType.values().length);
                OperationType operationType = OperationType.values()[randomIndex];
                operationTypeList.add(operationType);
            }
        }
        return operationTypeList;
    }

}
