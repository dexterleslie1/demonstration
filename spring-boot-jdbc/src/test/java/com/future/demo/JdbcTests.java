package com.future.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// https://mariadb.com/ko/resources/blog/how-to-benchmark-mariadb-mysql-using-java-connector/
@State(Scope.Benchmark)
@Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS, time = 1)
@Measurement(iterations = 5, timeUnit = TimeUnit.SECONDS, time = 15)
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(value = -1) // detecting CPU count
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class JdbcTests {

    @State(Scope.Thread)
    public static class MyState {

        private Connection connection;

//        @Param({"1", "10", "100", "1000", "10000"})
//        public int size;

        @Param({"false", "true"})
        public String binary;


        @Setup(Level.Trial)
        public void createConnections() throws Exception {
            String connectionString = String.format(
                    "jdbc:mariadb://192.168.1.193:50000/demo_db?user=root&password=123456&useServerPrepStmts=%s",
                    binary);
            connection = DriverManager.getConnection(connectionString);
        }

        @TearDown(Level.Trial)
        public void doTearDown() throws SQLException {
            connection.close();
        }
    }

    @Benchmark
    public Date[] testSeq(MyState state) throws SQLException {
        int i = 0;
//        int[] values = new int[state.size];
//        int values[] = new int[1];
        Date values[] = new Date[1];

//        try (PreparedStatement prep = state.connection.prepareStatement("select * from seq_1_to_" + state.size)) {
        try (PreparedStatement prep = state.connection.prepareStatement("select now()")) {
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
//                values[i++] = rs.getInt(1);
                values[i++] = rs.getDate(1);
            }
        }

        return values;
    }

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(JdbcTests.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();
        new Runner(opt).run();
    }
}