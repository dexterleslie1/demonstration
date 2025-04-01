package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoSpringBootAopApplicationTests {

    @Autowired
    MyCalculator myCalculator;
    @Autowired
    MyAspect myAspect;
    @Autowired
    SharedStore sharedStore;
    @Autowired
    AroundAspect aroundAspect;

    @Test
    void contextLoads() {
        // 测试没有异常
        int result = myCalculator.add(1, 2);
        Assertions.assertEquals(3, result);
        Assertions.assertTrue(myAspect.invokedBefore);
        Assertions.assertTrue(myAspect.invokedAfterReturning);
        Assertions.assertFalse(myAspect.invokedAfterThrowing);
        Assertions.assertTrue(myAspect.invokedAfter);
        Assertions.assertEquals("add", myAspect.methodName);
        Assertions.assertArrayEquals(new int[]{1, 2}, myAspect.args);
        Assertions.assertEquals(3, myAspect.result);
        Assertions.assertNull(myAspect.throwable);
        Assertions.assertArrayEquals(new String[]{"aspect1", "aspect2"}, sharedStore.sharedList.toArray());

        Assertions.assertTrue(aroundAspect.invokedBefore);
        Assertions.assertTrue(aroundAspect.invokedAfterReturning);
        Assertions.assertFalse(aroundAspect.invokedAfterThrowing);
        Assertions.assertTrue(aroundAspect.invokedAfter);

        myAspect.reset();
        aroundAspect.reset();

        // 测试抛出异常
        try {
            myCalculator.div(1, 0);
            Assertions.fail();
        } catch (ArithmeticException e) {
            Assertions.assertTrue(myAspect.invokedBefore);
            Assertions.assertFalse(myAspect.invokedAfterReturning);
            Assertions.assertTrue(myAspect.invokedAfterThrowing);
            Assertions.assertTrue(myAspect.invokedAfter);
            Assertions.assertEquals("div", myAspect.methodName);
            Assertions.assertArrayEquals(new int[]{1, 0}, myAspect.args);
            Assertions.assertNull(myAspect.result);
            Assertions.assertEquals("/ by zero", myAspect.throwable.getMessage());
            Assertions.assertArrayEquals(new String[]{"aspect1", "aspect2"}, sharedStore.sharedList.toArray());

            Assertions.assertTrue(aroundAspect.invokedBefore);
            Assertions.assertFalse(aroundAspect.invokedAfterReturning);
            Assertions.assertTrue(aroundAspect.invokedAfterThrowing);
            Assertions.assertTrue(aroundAspect.invokedAfter);
        } finally {
            myAspect.reset();
            aroundAspect.reset();
        }
    }

}
