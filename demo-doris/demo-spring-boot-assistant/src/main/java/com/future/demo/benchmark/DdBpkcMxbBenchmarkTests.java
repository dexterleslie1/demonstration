package com.future.demo.benchmark;

import com.future.demo.Application;
import com.future.demo.service.DdService;
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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 布匹库存明细表（InventoryMapper.bpkcMxb）查询延迟 JMH。
 * <p>
 * 每次 invocation 使用 {@link BpkcMxbQueryParam#random} 模拟用户不可预知的筛选组合
 * （对齐真实应用：无法预知参数，不依赖计划缓存命中）。
 * 需本地 demo-doris FE 9030 可用，且 demot.dd 中已有 company_id=751 数据
 * （可先跑 {@code DdGenerate100kTests}）。
 * </p>
 * <pre>
 * mvn -q -DskipTests package
 * java -jar target/benchmark-doris-dd.jar DdBpkcMxbBenchmarkTests
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(1)
public class DdBpkcMxbBenchmarkTests {

    private static final long COMPANY_ID = DdCompany751StyleGenerator.DEFAULT_COMPANY_ID;

    private ApplicationContext context;
    private DdService ddService;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DdBpkcMxbBenchmarkTests.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .jvmArgs("-Xmx1G", "-server")
                .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(Application.class);
        ddService = context.getBean(DdService.class);
        BpkcMxbQueryParam smokeParam = BpkcMxbQueryParam.random(COMPANY_ID);
        List<Map<String, Object>> smoke = ddService.queryBpkcMxb(smokeParam);
        System.out.println("[DdBpkcMxbBenchmarkTests] smoke rows=" + smoke.size()
                + ", param=" + smokeParam);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        ((ConfigurableApplicationContext) context).close();
    }

    @Benchmark
    public void bpkcMxbQuery(Blackhole bh) {
        // 每次新随机参数：模拟用户任意筛选（冷计划路径，不靠参数池预热）
        bh.consume(ddService.queryBpkcMxb(BpkcMxbQueryParam.random(COMPANY_ID)));
    }
}
