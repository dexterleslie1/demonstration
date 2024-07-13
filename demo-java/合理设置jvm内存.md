# 合理设置`jvm`内存

> [深入JVM虚拟机之高并发交易系统应如何设置JVM堆内存的大小？](https://blog.csdn.net/weixin_42194284/article/details/106912093)

## 理论分析

以一个每日百万级别的交易支付系统作为背景，来分析一下，在线上部署一个系统时，应该如何根据系统的业务来合理的设置JVM对内存的大小。

如果每日有百万笔交易，在JVM的角度来看，就是每天会在JVM中创建上百万个新的支付订单对象

假设每天有100万个支付订单一般用户交易行为会集中在几个小时内的高峰期中。

对于高峰期而言，每秒需要能够支持处理100笔订单左右。

假设我们的支付系统部署了3台机器，每台机器实际上每秒需要处理30笔订单。

支付订单需求

1. 一次支付请求，需要在JVM中创建一个支付订单对象，填充数据，然后办这个支付订单写入数据库，进行付款等等。
2. 假设一次支付请求的处理，从头到尾，总共大概需要1秒的时间。
3. 那么对于每台机器而言，一秒钟接受到30笔支付的请求，然后在JVM的新生代理创建了30 个支付订单对象。
4. 1秒钟之后，对这30个支付订单进行处理，就可以对它们的引用进行回收，这些订单对象就成为JVM新生代里面，没人引用的垃圾对象了。

支付对象大小：

1. 直接根据支付订单类中的实例变量的类型来计算，比如：
   - Integer类型的变量数据是4个字节
   - Long类型变量数据是8个字节
2. 一般来说，支付订单这种核心累呢，按照20-30个实例变量来计算，一个对象大概也就是在1kb左右。
3. 对于一台机器来说，每秒钟处理30笔支付订单的请求，大概占据的空间最多就是30*1kb而已。

运行过程

1. 但是真实的支付系统在运行过程中，肯定每秒还会创建大量的其他对象。我们估算一下，每秒钟除了支付订单对象，还会创建其他对象
2. 对于30个订单对象，需要30*1KB=30KB的内存空间，算上其他对象，那么每秒钟创建出来的对象，一共大概需要接近1MB左右。
3. 然后下一秒，对新的请求继续创建大概1MB左右的对象，放在新生代里面。
4. 循环多次后，新生代垃圾太多，就会触发Minor GC回收掉这些垃圾。

JVM堆内存设置

1. 其实一般来说线上业务系统，常见的机器配置是2核4G，或者是4核8G
2. 如果用2核4G的机器来部署，机器4G内存，JVM进程估计最多也就是2G内存。
3. 这2G还得分给元空间、栈内存、堆内存几块区域，那么堆内存估计可能也就1个多G的内存空间。堆内存还分为新生代和老年代，那么新生代可能也就几百MB的内存
4. 整个系统每秒需要1MB左右的内存空间，新生代只有几百MB，运行几百秒也就是大概五六分钟左右，新生代内存空间就满了，肯定会触发Minor GC。如果这么频繁的触发Minor GC，必然会影响线上系统的性能稳定性



## 证明上面理论分析

1. 编译[demo-java-assistant](https://github.com/dexterleslie1/demonstration/tree/master/demo-java/demo-java-assistant)演示

   ```bash
   mvn package
   ```

2. 运行演示

   ```bash
   java -jar target/demo.jar memallocpeak
   ```

3. 使用`jstat`或者`arthas memory`查看堆内存使用

4. 结论：通过查看堆内存使用情况，可以看到老年代内存使用接近`512m`符合以上理论预期



## 合理设置内存

1. 编译和运行[demo-springboot-performance](https://github.com/dexterleslie1/demonstration/tree/master/performance/jvm/demo-springboot-performance)演示，协助分析垃圾回收相关信息

   ```bash
   # 编译
   mvn package
   
   # 启动springboot项目
   # 使用-XX:NewSize=64m和-XX:MaxNewSize=64m设置过小的内存导致频繁YoungGC和FullGC
   java -jar -Xmx1g -Xms1g -XX:NewSize=64m -XX:MaxNewSize=64m -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:HeapDumpPath=./ target/demo-springboot-performance.jar
   ```

2. 使用`jmeter`打开并运行[memory负载.jmx](https://github.com/dexterleslie1/demonstration/blob/master/performance/jvm/demo-springboot-performance/memory%E8%B4%9F%E8%BD%BD.jmx)

3. 查看`jvm`内存和`GC`情况并通过`-Xmx -Xms`调整`jvm`内存

   ```bash
   tail -f gc.log.0.current 
   ```

4. 结论

   - `jvm`内存设置过低会报告`OOM`错误
   - `jvm`内存设置过低如果不报告`OOM`错误也会导致频繁`Full GC`影响并发性能
   - 暂时发现`PS+PO`、`CMS`、`G1`垃圾回收器对性能影响相同，所以使用默认的垃圾回收器即可
   - 使用`-XX:NewSize和-XX:MaxNewSize`设置过小的新生代区会导致较多的新生代`GC`，进而导致老年代区占用迅速膨胀，最后导致频繁`Full GC`影响并发性能
   - 线程设置数量和内存设置成正比，否则会导致较多的新生代`GC`，进而导致老年代区占用迅速膨胀，最后导致频繁`Full GC`影响并发性能
   - 如果不是内存泄漏问题，一般通过`-Xmx`、`-Xms`把内存调大能够解决频繁`GC`引起的稳定性和性能问题

