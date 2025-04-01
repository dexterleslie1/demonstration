package com.future.study.spring.amqp.acknowledge;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
public class Config {
    @Bean
    Queue queue() {
//        return new AnonymousQueue();
        return new Queue("testQueue1", true, false, false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(Const.ExchangeName, true, false);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routingKey1");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueues(queue());
        container.setMessageListener(listenerAdapter);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
