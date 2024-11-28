package com.future.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// https://mariadb.com/ko/resources/blog/how-to-benchmark-mariadb-mysql-using-java-connector/
@State(Scope.Benchmark)
@Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS, time = 1)
@Measurement(iterations = 5, timeUnit = TimeUnit.SECONDS, time = 15)
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(value = -1) // detecting CPU count
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class JdbcPerfTests {

    List<Long> userIdList = new ArrayList<>();

    @Setup
    public void setup() {
        for (long i = 1; i <= 200000; i++) {
            userIdList.add(i);
        }
    }

    @State(Scope.Thread)
    public static class MyState {

        private Connection connection;

//        @Param({"1", "10", "100", "1000", "10000"})
//        public int size;

//        @Param({"false", "true"})
//        public String binary;


        @Setup(Level.Trial)
        public void createConnections() throws Exception {
//            String connectionString = String.format(
//                    "jdbc:mariadb://localhost:50000/demo_db?user=root&password=123456&useServerPrepStmts=%s",
//                    binary);
            String connectionString = "jdbc:mariadb://localhost:50000/demo_db?user=root&password=123456";
            connection = DriverManager.getConnection(connectionString);
        }

        @TearDown(Level.Trial)
        public void doTearDown() throws SQLException {
            connection.close();
        }
    }

    Random random = new Random();

    @Benchmark
    public List<List<Object>> testSeq(MyState state) throws SQLException {
        int randomIndex = random.nextInt(userIdList.size());
        Long userId = userIdList.get(randomIndex);
        int randomPage = random.nextInt(100);
        int randomSize = random.nextInt(100);
        if (randomSize <= 0)
            randomSize = 1;

        List<OperationType> operationTypeList = this.getRandomOperationTypeList();

        if (randomPage <= 0) {
            randomPage = 1;
        }

        int start = (randomPage - 1) * randomSize;
        String sqlSelect = "select * from operation_log where auth_id=?";
        if (operationTypeList != null && operationTypeList.size() > 0) {
            sqlSelect = sqlSelect + " and operation_type in (?)";
        }
        sqlSelect = sqlSelect + " order by id desc limit ?,?";

        int i = 0;
        List<List<Object>> resultsetList = new ArrayList<>();
        try (PreparedStatement prep = state.connection.prepareStatement(sqlSelect)) {
            prep.setLong(1, userId);
            if (operationTypeList != null && operationTypeList.size() > 0) {
                prep.setObject(2, operationTypeList);
                prep.setInt(3, start);
                prep.setInt(4, randomSize);
            } else {
                prep.setInt(2, start);
                prep.setInt(3, randomSize);
            }

            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                resultsetList.add(new ArrayList<Object>() {{
                    add(rs.getLong(1));
                }});
            }
        }

        return resultsetList;
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

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(JdbcPerfTests.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();
        new Runner(opt).run();
    }
}