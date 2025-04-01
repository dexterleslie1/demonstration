# `awaitility`使用

>[Github Getting Started](https://github.com/awaitility/awaitility/wiki/Getting_started)
>
>[Github Usage](https://github.com/awaitility/awaitility/wiki/Usage)

`Awaitility` 是一个 Java 库，用于在编写异步代码时简化等待条件成立的过程。它提供了一种流畅的 API 来等待直到某个条件为真，而无需编写复杂的轮询逻辑或处理线程中断。这对于测试异步代码或等待外部系统响应时特别有用。

`awaitility`的详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-awaitility)

`maven`配置引用`awaitility`

```xml
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <version>4.1.1</version>
    <scope>test</scope>
</dependency>
```

示例：

```java
long milliseconds = 5000;

//region 测试等待成功

AtomicInteger atomicInteger = new AtomicInteger();
AtomicInteger finalAtomicInteger1 = atomicInteger;
Thread thread = new Thread(() -> {
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    finalAtomicInteger1.incrementAndGet();
});
thread.start();

// 最长等待5秒
// 每1秒检查一次条件是否满足
Awaitility.await().atMost(milliseconds, TimeUnit.MILLISECONDS).pollInterval(Duration.ofSeconds(1)).until(() -> {
    System.out.println(new Date());
    return finalAtomicInteger1.get() >= 1;
});

//endregion
```

示例中使用`awaitility`等待`return finalAtomicInteger1.get() >= 1;`条件满足才继续执行代码，如果等待超过5秒条件仍然未满足则抛出异常。