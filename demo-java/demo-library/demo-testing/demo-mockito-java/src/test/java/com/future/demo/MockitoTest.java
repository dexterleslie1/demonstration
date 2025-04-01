package com.future.demo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Dexterleslie
 * @date 2018年8月2日
 * @time 上午11:21:41
 */
public class MockitoTest {
    /**
     * 测试mock对象是否已调用指定函数
     */
    @Test
    public void verify_if_function_called() {
        // 创建List mock对象
        List mockListObject = Mockito.mock(List.class);

        mockListObject.add("val1");
        mockListObject.remove(0);
        mockListObject.clear();

        // 验证add("val1")、remove(0)、removeAll()是否被调用
        Mockito.verify(mockListObject).add("val1");
        Mockito.verify(mockListObject).remove(0);
        Mockito.verify(mockListObject).clear();

        // https://stackoverflow.com/questions/14889951/how-to-verify-a-method-is-called-two-times-with-mockito-verify
        // 验证方法被调用指定次数
        Mockito.verify(mockListObject, Mockito.times(1)).add("val1");
        mockListObject.add("val1");
        Mockito.verify(mockListObject, Mockito.times(2)).add("val1");
    }

    /**
     * 模拟抛出异常
     */
    @Test(expected = Exception.class)
    public void throw_exception() {
        List mockListObject = Mockito.mock(List.class);
        Mockito.doThrow(new Exception("模拟抛出异常")).when(mockListObject).get(1);

        mockListObject.get(0);
        mockListObject.get(1);
    }

    /**
     * Answer使用
     */
    @Test
    public void answerTest() {
        List mockList = Mockito.mock(List.class);
        Mockito.when(mockList.get(Mockito.anyInt())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return "hello world:" + args[0];
            }
        });
        Assert.assertEquals("hello world:0", mockList.get(0));
        Assert.assertEquals("hello world:999", mockList.get(999));
    }

    /**
     * 验证调用次数或者未调用
     */
    @Test
    public void verifying_number_of_invocations() {
        List list = Mockito.mock(List.class);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(3);
        list.add(3);
        //验证是否被调用一次，等效于下面的times(1)
        Mockito.verify(list).add(1);
        Mockito.verify(list, Mockito.times(1)).add(1);
        //验证是否被调用2次
        Mockito.verify(list, Mockito.times(2)).add(2);
        //验证是否被调用3次
        Mockito.verify(list, Mockito.times(3)).add(3);
        //验证是否从未被调用过
        Mockito.verify(list, Mockito.never()).add(4);
        //验证至少调用一次
        Mockito.verify(list, Mockito.atLeastOnce()).add(1);
        //验证至少调用2次
        Mockito.verify(list, Mockito.atLeast(2)).add(2);
        //验证至多调用3次
        Mockito.verify(list, Mockito.atMost(3)).add(3);
    }

    /**
     * 验证调用顺序
     */
    @Test
    public void verification_in_order() {
        List list = Mockito.mock(List.class);
        List list2 = Mockito.mock(List.class);
        list.add(1);
        list2.add("hello");
        list.add(2);
        list2.add("world");
        //将需要排序的mock对象放入InOrder
        InOrder inOrder = Mockito.inOrder(list, list2);
        //下面的代码不能颠倒顺序，验证执行顺序
        inOrder.verify(list).add(1);
        inOrder.verify(list2).add("hello");
        inOrder.verify(list).add(2);
        inOrder.verify(list2).add("world");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void spy_on_real_objects() {
        List list = new LinkedList();
        List spy = Mockito.spy(list);
        //下面预设的spy.get(0)会报错，因为会调用真实对象的get(0)，所以会抛出越界异常
//        Mockito.when(spy.get(0)).thenReturn(3);

        //使用doReturn-when可以避免when-thenReturn调用真实对象api
        Mockito.doReturn(999).when(spy).get(999);
        //预设size()期望值
        Mockito.when(spy.size()).thenReturn(100);
        //调用真实对象的api
        spy.add(1);
        spy.add(2);
        Assert.assertEquals(100, spy.size());
        Assert.assertEquals(1, spy.get(0));
        Assert.assertEquals(2, spy.get(1));
        Mockito.verify(spy).add(1);
        Mockito.verify(spy).add(2);
        Assert.assertEquals(999, spy.get(999));
        spy.get(2);
    }

    @Test
    public void real_partial_mock() {
        //通过spy来调用真实的api
        List list = Mockito.spy(new ArrayList());
        Assert.assertEquals(0, list.size());
        A a = Mockito.mock(A.class);
        //通过thenCallRealMethod来调用真实的api
        Mockito.when(a.doSomething(Mockito.anyInt())).thenCallRealMethod();
        Assert.assertEquals(999, a.doSomething(999));
    }

    class A {
        public int doSomething(int i) {
            return i;
        }
    }

    /**
     * 测试doReturn和doAnswer的区别
     */
    @Test
    public void testDifferentWithDoReturnAndDoAnswer() {
        List<String> testList = Mockito.mock(List.class);

        // 测试doReturn
        Mockito.doReturn("hello").when(testList).get(Mockito.anyInt());
        String str = testList.get(0);
        Assert.assertEquals("hello", str);

        // 测试doAnswer
        Mockito.doAnswer(invocationOnMock -> {
            // 根据输入参数返回hello-x字符串
            return "hello-" + invocationOnMock.getArguments()[0];
        }).when(testList).get(Mockito.anyInt());

        str = testList.get(0);
        Assert.assertEquals("hello-0", str);

        str = testList.get(1);
        Assert.assertEquals("hello-1", str);
    }

    /**
     * 演示mock final类
     */
    @Test
    public void testMockFinalClass() {
        MyFinalClass myFinalClass = Mockito.mock(MyFinalClass.class);
        Mockito.doReturn("H").when(myFinalClass).sayHello();

        String str = myFinalClass.sayHello();
        Assert.assertEquals("H", str);
    }

    // 使用Mockito.clearInvocations清除之前的mock调用信息
    // https://stackoverflow.com/questions/30081161/mockito-does-verify-method-reboot-number-of-times
    @Test
    public void testClearInvocations() {
        List mockList = Mockito.mock(List.class);
        mockList.add("1");
        Mockito.verify(mockList).add("1");

        mockList.add("2");
        Mockito.verify(mockList).add("2");
        Mockito.verify(mockList).add("1");

        // 使用clearInvocations清除之前的调用信息
        Mockito.clearInvocations(mockList);
        mockList.add("2");
        Mockito.verify(mockList).add("2");
        try {
            // 被clearInvocations，所以之前add("1")的调用信息不存在
            Mockito.verify(mockList).add("1");
            Assert.fail("预期异常没有抛出");
        } catch (Throwable throwable) {
            Assert.assertTrue(throwable instanceof WantedButNotInvoked);
        }
    }

    /**
     * 验证调用时的参数
     * https://stackoverflow.com/questions/3555472/mockito-verify-method-arguments
     * https://ioflood.com/blog/mockito-verify/
     */
    @Test
    public void testArgumentCaptor() {
        List mockList = Mockito.mock(List.class);
        mockList.add(new MyArgument("Dexter"));

        ArgumentCaptor<MyArgument> argumentCaptor = ArgumentCaptor.forClass(MyArgument.class);
        Mockito.verify(mockList).add(argumentCaptor.capture());
        // 验证方法使用预期的参数调用
        Assert.assertEquals("Dexter", argumentCaptor.getValue().getName());
    }

    public static class MyArgument {
        private String name;

        public MyArgument(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
