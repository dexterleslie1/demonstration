package com.future.demo;

import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(256)
public class PerfTests {

    final static Random RANDOM = new Random(System.currentTimeMillis());

    //springBoot容器
    ApplicationContext context;

    OrderService orderService;
    ProductMapper productMapper;
    OrderMapper orderMapper;
    StringRedisTemplate redisTemplate;

//    List<Long> productIdList = null;

    // 100,10 表示共 100 个商品，每个商品库存为 10 个
//    @Param(value = {"100,10", "1000,1000000"})
//    String totalProductCountAndProductStockStr;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(PerfTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx4G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() {
        //容器获取
        context = SpringApplication.run(Application.class);
        //获取对象
        orderService = context.getBean(OrderService.class);
        productMapper = context.getBean(ProductMapper.class);
        orderMapper = context.getBean(OrderMapper.class);
        redisTemplate = context.getBean(StringRedisTemplate.class);

//        Object[] objs = this.parseParam(totalProductCountAndProductStockStr);
//        int totalProductCount = (int) objs[0];
//        int productStock = (int) objs[1];
//        this.reset(totalProductCount, productStock);
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

//    @Benchmark
//    public void testCreateOrderBasedDB() {
//        Long productId = productIdList.get(RANDOM.nextInt(productIdList.size()));
//        Long userId = RANDOM.nextLong();
//        Integer amount = 2;
//        try {
//            orderService.createOrderBasedDB(userId, productId, amount);
//        } catch (Exception ex) {
//            if (!ex.getMessage().contains("库存不足")
//                    && !ex.getMessage().contains("重复下单")
//                    && !ex.getMessage().contains("扣减")) {
//                ex.printStackTrace();
//            }
//        }
//    }

    @Benchmark
    public void testCreateOrderBasedRedisWithLuaScript() {
        long productId = RANDOM.nextInt(OrderService.ProductCount) + 1L;
        long userId = RANDOM.nextLong();
        Integer amount = 1;
        try {
            orderService.createOrderBasedRedisWithLuaScript(userId, productId, amount);
        } catch (Exception ex) {
            if (!ex.getMessage().contains("库存不足")
                    && !ex.getMessage().contains("重复下单")
                    && !ex.getMessage().contains("扣减")) {
                ex.printStackTrace();
            }
        }
    }

//    @Benchmark
//    public void testCreateOrderBasedRedisWithoutLuaScript() {
//        Long productId = productIdList.get(RANDOM.nextInt(productIdList.size()));
//        Long userId = RANDOM.nextLong();
//        Integer amount = 2;
//        try {
//            orderService.createOrderBasedRedisWithoutLuaScript(userId, productId, amount);
//        } catch (Exception ex) {
//            if (!ex.getMessage().contains("库存不足")
//                    && !ex.getMessage().contains("重复下单")
//                    && !ex.getMessage().contains("扣减")) {
//                ex.printStackTrace();
//            }
//        }
//    }

    void reset(int totalProductCount, int productStock) {
        /*productIdList = new ArrayList<>();
        for (long i = 2; i <= totalProductCount; i++) {
            ProductModel productModel = new ProductModel();
            productModel.setId(i);
            productModel.setName("商品" + i);
            productModel.setStock(productStock);
            this.productMapper.insert(productModel);
            productIdList.add(i);

            this.productMapper.updateStock(i, productStock);

            // 秒杀前提前加载商品库存信息到 Redis 中
            *//*String keyProductStock = OrderService.KeyProductStockPrefix + productModel.getId();
            this.redisTemplate.opsForValue().set(keyProductStock, productModel.getStock().toString());
            String keyProductPurchaseRecord = "product:purchase:" + i;
            this.redisTemplate.delete(keyProductPurchaseRecord);
            String key = OrderService.KeyProductSoldOutPrefix + i;
            this.redisTemplate.delete(key);*//*
        }
        // 删除所有订单
        this.orderMapper.deleteAll();*/
    }

    /*Object[] parseParam(String totalProductCountAndProductStockStr) {
        String[] strs = totalProductCountAndProductStockStr.split(",");
        int totalProductCount = Integer.parseInt(strs[0]);
        int productStock = Integer.parseInt(strs[1]);
        return new Object[]{totalProductCount, productStock};
    }*/
}