package com.future.demo;

import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTests {

    @Test
    public void test() throws InterruptedException {
        // region add、remove，add 已满抛出异常，remove 为空抛出异常
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);
        Assert.assertTrue(blockingQueue.add("a"));
        Assert.assertTrue(blockingQueue.add("b"));
        try {
            // 队列已满抛出异常
            blockingQueue.add("c");
            Assert.fail();
        } catch (IllegalStateException ignored) {

        }

        Assert.assertEquals("a", blockingQueue.remove());
        Assert.assertEquals("b", blockingQueue.remove());
        try {
            // 队列为空抛出异常
            Assert.assertNull(blockingQueue.remove());
            Assert.fail();
        } catch (NoSuchElementException ignored) {

        }

        // endregion

        // region offer、poll，offer 已满返回 false，poll 为空返回 null

        blockingQueue = new ArrayBlockingQueue<>(2);
        Assert.assertTrue(blockingQueue.offer("a"));
        Assert.assertTrue(blockingQueue.offer("b"));
        Assert.assertFalse(blockingQueue.offer("c"));

        Assert.assertEquals("a", blockingQueue.poll());
        Assert.assertEquals("b", blockingQueue.poll());
        Assert.assertNull(blockingQueue.poll());

        blockingQueue = new ArrayBlockingQueue<>(2);
        Assert.assertTrue(blockingQueue.offer("a"));
        Assert.assertTrue(blockingQueue.offer("b"));
        Assert.assertFalse(blockingQueue.offer("c", 100, TimeUnit.MILLISECONDS));

        Assert.assertEquals("a", blockingQueue.poll());
        Assert.assertEquals("b", blockingQueue.poll());
        Assert.assertNull(blockingQueue.poll(100, TimeUnit.MILLISECONDS));

        // endregion

        // region put、take，put 已满阻塞，take 为空阻塞

        blockingQueue = new ArrayBlockingQueue<>(2);
        blockingQueue.put("a");
        blockingQueue.put("b");
        // 注意：阻塞
        /*blockingQueue.put("c");*/

        Assert.assertEquals("a", blockingQueue.take());
        Assert.assertEquals("b", blockingQueue.take());
        // 注意：阻塞
        /*blockingQueue.take();*/

        // endregion
    }
}
