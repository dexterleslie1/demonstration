package com.future.study.activemq.basic.usage;

import org.apache.activemq.util.Suspendable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.Date;

/**
 * Examples for demonstrating basic usage of activemq java client
 * Reference sites:
 * https://codenotfound.com/jms-publish-subscribe-messaging-example-activemq-maven.html
 * @author Dexterleslie.Chan
 */
public class BasicUsageExampleTest {
    @Test
    public void test_publish_subscribe() throws JMSException {
        String stringTimeNow = (new Date()).toString();
        publisher.sendMessage(stringTimeNow);

        String message = subscriber.receiveMessage(1000);
        Assert.assertEquals(stringTimeNow, message);
    }

    private Publisher publisher = null;
    private Subscriber subscriber = null;

    @Before
    public void setup() throws JMSException {
        String topic = "topic-publish-subscribe";
        publisher = new Publisher("publisher-one", topic);
        publisher.connect();

        subscriber = new Subscriber("subscriber-one", topic);
        subscriber.connect();
    }

    @After
    public void teardown() throws JMSException {
        publisher.close();
        subscriber.close();
    }
}
