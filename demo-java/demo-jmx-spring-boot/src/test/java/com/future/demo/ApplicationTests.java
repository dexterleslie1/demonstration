package com.future.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class ApplicationTests {
    @Autowired
    MeterRegistry registry;

    @Test
    public void testCounterApi() {
        Counter counter = Counter
                .builder("instance")
                .description("indicates instance count of the object")
                .tags("dev", "performance")
                .register(registry);
        counter.increment(2.0);
        Assertions.assertTrue(counter.count() == 2);
        counter.increment(-1);
        Assertions.assertTrue(counter.count() == 2);
    }

    @Test
    public void testGaugeApi() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        List<String> list = new ArrayList<>(4);
        Gauge gauge = Gauge
                .builder("cache.size", list, List::size)
                .register(registry);
        Assertions.assertTrue(gauge.value() == 0.0);
        list.add("1");
        list.add("2");
        Assertions.assertTrue(gauge.value() == 2.0);

        list.remove("1");
        Assertions.assertTrue(gauge.value() == 1.0);
        list.remove("2");
        Assertions.assertTrue(gauge.value() == 0.0);

        AtomicInteger atomicInteger = new AtomicInteger(0);
        gauge = Gauge.builder("integer.counter", atomicInteger, AtomicInteger::get)
                .register(registry);
        atomicInteger.incrementAndGet();
        Assertions.assertTrue(gauge.value() == 1.0);
        atomicInteger.incrementAndGet();
        Assertions.assertTrue(gauge.value() == 2.0);
        atomicInteger.decrementAndGet();
        Assertions.assertTrue(gauge.value() == 1.0);
        atomicInteger.set(0);
        Assertions.assertTrue(gauge.value() == 0.0);
    }

    /**
     * 测试动态标签场景使用 builder 和相同的参数是否会返回相同的实例
     */
    @Test
    public void testDynamicTagsCheckIfBuilderReturnTheSameInstance() {
        // region 测试 Timer

        Timer timer1 = Timer.builder("http_request_duration")
                .description("Request latency in seconds.")
                // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(500),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(30000)
                )
                .tags("k1", "v1", "k2", "v2")
                .register(registry);

        Timer timer2 = Timer.builder("http_request_duration")
                .description("Request latency in seconds.")
                // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(500),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(30000)
                )
                .tags("k1", "v1", "k2", "v2")
                .register(registry);
        Assertions.assertEquals(timer1, timer2);

        // 测试 tags 不一样时不是同一个实例
        Timer timer3 = Timer.builder("http_request_duration")
                .description("Request latency in seconds.")
                // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(500),
                        Duration.ofMillis(1000),
                        Duration.ofMillis(5000),
                        Duration.ofMillis(10000),
                        Duration.ofMillis(30000)
                )
                .tags("k1", "v1", "k2", "v21")
                .register(registry);
        Assertions.assertNotEquals(timer1, timer3);

        // 测试桶分布不一样时也是同一个实例
        Timer timer4 = Timer.builder("http_request_duration")
                .description("Request latency in seconds.")
                // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(300),
                        Duration.ofMillis(500),
                        Duration.ofMillis(1000),
                        /*Duration.ofMillis(5000),*/
                        Duration.ofMillis(10000),
                        Duration.ofMillis(30000)
                )
                .tags("k1", "v1", "k2", "v2")
                .register(registry);
        Assertions.assertEquals(timer1, timer4);

        // endregion

        // region 测试 Counter

        Counter counter1 = Counter.builder("counter")
                .description("desc1")
                .tags("k1", "v1", "k2", "v2")
                .register(registry);
        Counter counter2 = Counter.builder("counter")
                .description("desc1")
                .tags("k1", "v1", "k2", "v2")
                .register(registry);
        Assertions.assertEquals(counter1, counter2);

        // 测试描述不一样时也是同一个实例
        Counter counter3 = Counter.builder("counter")
                .description("desc11")
                .tags("k1", "v1", "k2", "v2")
                .register(registry);
        Assertions.assertEquals(counter1, counter3);

        // 测试名称不一样时不是同一个实例
        Counter counter4 = Counter.builder("counter1")
                .description("desc1")
                .tags("k1", "v1", "k2", "v2")
                .register(registry);
        Assertions.assertNotEquals(counter1, counter4);

        // 测试 tags 不一样时不是同一个实例
        Counter counter5 = Counter.builder("counter")
                .description("desc1")
                .tags("k1", "v1", "k2", "v21")
                .register(registry);
        Assertions.assertNotEquals(counter1, counter5);

        // endregion

        // region 测试 Gauge

        Gauge gauge1 = Gauge.builder("gauge", new Supplier<Number>() {
            @Override
            public Number get() {
                return null;
            }
        }).tags("k1", "v1", "k2", "v2").register(registry);
        Gauge gauge2 = Gauge.builder("gauge", new Supplier<Number>() {
            @Override
            public Number get() {
                return null;
            }
        }).tags("k1", "v1", "k2", "v2").register(registry);
        Assertions.assertEquals(gauge1, gauge2);

        // 测试 tags 不一样时不是同一个实例
        Gauge gauge3 = Gauge.builder("gauge", new Supplier<Number>() {
            @Override
            public Number get() {
                return null;
            }
        }).tags("k1", "v1", "k2", "v21").register(registry);
        Assertions.assertNotEquals(gauge1, gauge3);

        // endregion
    }
}
