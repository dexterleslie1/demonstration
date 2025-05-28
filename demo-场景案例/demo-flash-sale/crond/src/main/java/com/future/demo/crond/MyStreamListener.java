//package com.future.demo.crond;
//
//import com.future.demo.service.OrderService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.stream.MapRecord;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.stream.StreamListener;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//@Component
//@Slf4j
//public class MyStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
//    @Autowired
//    StringRedisTemplate redisTemplate;
//    @Resource
//    OrderService orderService;
//
//    @Override
//    public void onMessage(MapRecord<String, String, String> message) {
//        try {
//            log.info("收到消息：{}", message);
//
//            Long userId = Long.parseLong(message.getValue().get("userId"));
//            Long productId = Long.parseLong(message.getValue().get("productId"));
//            Integer amount = Integer.parseInt(message.getValue().get("amount"));
//            /*this.orderService.create(userId, productId, amount);*/
//            this.redisTemplate.delete(String.valueOf(userId));
//
//            /*this.redisTemplate.opsForStream().acknowledge(Const.GroupName, message);*/
//            //this.redisTemplate.opsForStream().acknowledge(message.getStream(), Const.GroupName, message.getId());
//        } catch (Throwable throwable) {
//            log.error(throwable.getMessage(), throwable);
//        }
//    }
//}
