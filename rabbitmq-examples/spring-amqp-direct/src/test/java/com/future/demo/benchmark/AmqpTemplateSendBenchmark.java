package com.future.demo.benchmark;

import com.future.demo.Application;
import com.future.demo.BenchmarkDirectReceiver;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AmqpTemplate#convertAndSend 发送消息性能测试。
 * <p>
 * 运行前需确保 application.properties 中配置的 RabbitMQ 可用。
 * 消息发到 {@link BenchmarkDirectReceiver} 的 direct 交换机，由该消费者批量确认。
 * 本测试只统计发送调用耗时，不校验消费者是否收齐。
 * </p>
 * <pre>
 * cd rabbitmq-examples/spring-amqp-direct
 * mvn test-compile exec:java -Dexec.classpathScope=test \
 *   -Dexec.mainClass="com.future.demo.benchmark.AmqpTemplateSendBenchmark"
 * </pre>
 * <p>
 * IDEA 直接运行需开启 Annotation Processing，并先 Build -&gt; Rebuild Project，
 * 确保 test-classes/META-INF/BenchmarkList 已生成。
 * </p>
 */
@BenchmarkMode({Mode.Throughput})
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {
        "-Xms512m", "-Xmx512m", "-server",
        "-Dlogback.configurationFile=logback-benchmark.xml",
        "-Dlogging.level.root=ERROR"
})
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(8)
public class AmqpTemplateSendBenchmark {

    private static final String[] SPRING_BENCHMARK_ARGS = {
            "--server.port=0",
            "--logging.level.root=ERROR",
            "--logging.level.com.future=ERROR",
            "--logging.level.org.springframework=ERROR"
    };

    static {
        System.setProperty("logback.configurationFile", "logback-benchmark.xml");
        System.setProperty("logging.level.root", "ERROR");
    }

    private AmqpTemplate amqpTemplate;
    private ApplicationContext context;
    private final AtomicLong sequence = new AtomicLong(1);

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AmqpTemplateSendBenchmark.class.getSimpleName())
                .shouldFailOnError(true)
                .jvmArgs(
                        "-Dlogback.configurationFile=logback-benchmark.xml",
                        "-Dlogging.level.root=ERROR"
                )
                .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setupTrial() {
        context = new SpringApplication(Application.class).run(SPRING_BENCHMARK_ARGS);
        amqpTemplate = context.getBean(AmqpTemplate.class);
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        if (context instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) context).close();
        }
    }

    @Benchmark
    public void convertAndSend(Blackhole blackhole) {
        long seq = sequence.getAndIncrement();
        String message = "Hello from RabbitMQ!" + seq;
        amqpTemplate.convertAndSend(BenchmarkDirectReceiver.ExchangeName, BenchmarkDirectReceiver.RoutingKey, message);
        blackhole.consume(message);
    }
}
