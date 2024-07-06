# `GC`相关

## 为什么需要`GC`调优？

垃圾收集过程中，JVM需要暂停应用程序的执行（Stop-The-World），以便垃圾收集器能够遍历和回收堆内存中的无用对象。频繁的GC或长时间的GC停顿会严重影响应用程序的响应时间和吞吐量。通过优化GC，可以减少GC的频率和每次GC的停顿时间，从而提升整体性能。

## 什么是`MinorGC/YoungGC`、`MajorGC/OldGC`、`FullGC`？

- `MinorGC/YoungGC`

  其实所谓的 MinorGC，也可以称为 YoungGC，这二者是相同的，都是专门针对于新生代的GC.

  「新生代」也可以称为「年轻代」，在新生代的Eden内存区域被占满之后，就会触发「新生代」的GC，或者叫「年轻代」的GC，也就是所谓的 MinorGC 和 YoungGC

- `FullGC`

  从字面上意思其实就可以理解了，“Full” 就是整个的，完整的意思，所以就是**对 JVM 进行一次整体 垃圾回收**，把各个内存空间区域，不管是新生代，老年代，永久代的垃圾统统都回收掉。

- `MajorGC/OldGC`

  OldGC就是老年代的gc

https://www.sohu.com/a/436792112_216476

- 垃圾回收算法

  https://zhuanlan.zhihu.com/p/112018694

  标记清除法，适用于老年代使用
  标记复制法，适用于年轻代使用
  标记整理法，适用于老年代内存碎片问题使用

- 垃圾回收器

  Serial：应用于年轻代、单线程、采用标记复制法

  Serial Old：应用于老年代、单线程、采用标记整理法

  Parallel Scavenge，简称PS：应用于年轻代、多线程、采用标记复制法

  Parallel Old，简称PO：应用于老年代、多线程、采用标记整理法

  ParNew：应用于年轻代、多线程、采用标记复制法、与CMS配合使用

  CMS，又称Concurrent Mark Sweep：应用于老年代、多线程和业务线程并发、采用标记清除法

  

  CMS 算法对象消失问题解决方案（多标和错标），https://baijiahao.baidu.com/s?id=1719715247743913571&wfr=spider&for=pc，https://blog.csdn.net/weixin_45970271/article/details/123508686

  - 重新标记阶段
  - Incremental Update和Snapshot At The Begining

  todo G1

- GC 相关命令

  查看当前默认使用的垃圾回收器

  ```sh
  java -XX:+PrintCommandLineFlags
  ```

  查看支持的非标准参数列表

  ```sh
  java -XX:+PrintFlagsFinal
  ```

  打印GC日志到文件

  https://www.baeldung.com/java-gc-logging-to-file
  https://stackoverflow.com/questions/3822097/rolling-garbage-collector-logs-in-java
  https://blog.gceasy.io/2016/11/15/rotating-gc-log-files/

  ```sh
  varDirectory=`date '+%Y-%m-%d-%H-%M-%S'` && mkdir $varDirectory && java -jar -XX:+UseSerialGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./$varDirectory/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m java-performance.jar
  ```

  内存溢出时HeapDumpOnOutOfMemoryError

  https://stackoverflow.com/questions/542979/using-heapdumponoutofmemoryerror-parameter-for-heap-dump-for-jboss

  ```bash
  java -jar -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data -Xmx512m -Xms512m java-performance.jar
  ```

- 指定 JVM 垃圾回收器

  http://t.zoukankan.com/tiancai-p-14460214.html
  https://www.baeldung.com/jvm-garbage-collectors

  

  GC 日志格式解读

  https://plumbr.io/blog/garbage-collection/understanding-garbage-collection-logs

  G1日志格式
  https://blog.csdn.net/goldenfish1919/article/details/97747701

  

  指定 Serial

  ```sh
  java -jar -XX:+UseSerialGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:HeapDumpPath=./ -Xmx2g -Xms2g java-performance.jar
  ```

  

  指定 PS + PO

  ```sh
  java -jar -XX:+UseParallelGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:HeapDumpPath=./ -Xmx2g -Xms2g java-performance.jar
  ```

  

  指定 ParNew + CMS

  ```sh
  java -jar -XX:+UseConcMarkSweepGC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:HeapDumpPath=./ -Xmx2g -Xms2g java-performance.jar
  ```

  

  指定 G1

  ```sh
  java -jar -XX:+UseG1GC -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:HeapDumpPath=./ -Xmx2g -Xms2g java-performance.jar
  ```

  

- 使用 jprofiler 分析 heapdump 文件

  为何选择jprofiler？因为jhat解析1g以上的heapdump非常慢，mat解析8g以上的heapdump会报告OOM

  

  术语：

  - Shallow Size和Retained Size，https://www.jianshu.com/p/851b5bb0a4d4
  - outgoing references和incoming references，https://blog.csdn.net/qq_22222499/article/details/100069126

  

  找出可疑的内存泄露步骤：

  - 使用heapwalker的classes视图找出数量最多或者占用字节最多的class
  - 右键使用当前选中的class导航到References(Merged incoming references)视图，使用该视图找出对象的引用树

  

- 疑问

  为什么要有两个survivor区域
  https://zhuanlan.zhihu.com/p/265220301