//package com.future.demo;
//
//import com.future.demo.config.Config;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.util.UUID;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static com.future.demo.config.Config.TopicName;
//
//@DirtiesContext
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {ApplicationService.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//public class ApplicationTests {
//
//    @Resource
//    private KafkaTemplate<String, String> kafkaTemplate;
//    @Resource
//    Config config;
//
//    @Test
//    public void test() throws Exception {
//        final int count = 2048 + 50;
//
//        // 设置CountDownLatch
//        this.config.countDownLatch = new CountDownLatch(count);
//        Assert.assertEquals(count, this.config.countDownLatch.getCount());
//
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        executorService.submit(() -> {
//            for (int i = 0; i < count; i++) {
//                String message = UUID.randomUUID().toString();
//                kafkaTemplate.send(TopicName, message);
//            }
//        });
//
//        executorService.shutdown();
//        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
//
////        Assert.assertTrue(this.config.countDownLatch.await(5, TimeUnit.SECONDS));
////        Assert.assertEquals(0, this.config.countDownLatch.getCount());
//        TimeUnit.SECONDS.sleep(15);
//    }
//
//}
