package com.future.study.spring.amqp.acknowledge;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class Receiver implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(2);


    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        try {
            System.out.println("Received <" + message + ">");
            latch.countDown();

            // 下面代码用于模拟节点来不及ack消息就被关闭情况，此时消息应该会自动被路由器重新发送到另外一个匿名队列处理
//             Thread.sleep(3600000);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception ex) {
            ex.printStackTrace();
            // 其他异常重试这个消息直到成功处理 
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
