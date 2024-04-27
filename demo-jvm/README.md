## JVM

### java 从编码 -> 编译 -> 执行的过程

todo class 文件格式
类加载器

### Metaspace与PermGen

参考
https://www.jianshu.com/p/da41cc4122ff

### java 命令参数

- -XX:+CrashOnOutOfMemoryError 和 -XX:+ExitOnOutOfMemoryError

  NOTE：如果 crash 文件（例如：hs_err_pid7.log）已经存在，则 JVM 内存溢出崩溃时不会 overwrite 这个文件，所以要确保目录中不存在 hs_err_*.log 文件。

  

  内存溢出后 jvm 退出
  https://stackoverflow.com/questions/19433753/java-heap-dump-shut-down-what-order

  

  -XX:+CrashOnOutOfMemoryError 在 jvm 退出时打印崩溃日志。

  -XX:+ExitOnOutOfMemoryError 在 jvm 退出时不做任何动作。

  

### GC 相关

- MinorGC/YoungGC、MajorGC/OldGC、FullGC概念和触发时机

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

  ```
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

  

  

## jdk 相关命令

### jps

参考
https://blog.csdn.net/wisgood/article/details/38942449
https://docs.oracle.com/javase/7/docs/technotes/tools/share/jps.html

显示本机所有java进程，-m 显示传递给main方法的参数，-l 显示main方法包的完整路径或者jar文件的完整路径，-v 显示传递给jvm的参数

```sh
jps -mlv
```



- 配置支持远程jps命令

  启动本地jstatd（rmi协议）服务，使本地jvm可以被jps、visualVM（jconsole连接不了）远程监控
  https://blog.csdn.net/p358278505/article/details/81213747

  

  创建文件内容如下

  ```sh
  vi /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.232.b09-0.el7_7.x86_64/bin/jstats.all.policy
  ```

  ```json
  grant codebase "file:${java.home}/../lib/tools.jar" {
  	permission java.security.AllPermission;
  };
  ```

  

  启动jstatd服务

  ```sh
  ./jstatd -J-Djava.security.policy=jstatd.all.policy -J-Djava.rmi.server.hostname=192.168.1.173
  ```

  

  客户机远程连接jstatd服务

  ```sh
  jps -mlv rmi://192.168.1.173:19000
  ```

  

  注意：实质上jconsole不能远程连接jstatd，使用visualVM远程连接jstatd后，不能查看cpu、thread情况，似乎此远程rmi方案还不如jmx方案，所以暂时放弃不使用此方案远程监控，继续使用jmx远程监控方法

### jinfo

这个命令作用是实时查看和调整虚拟机运行参数。 之前的jps -v口令只能查看到显示指定的参数，如果想要查看未被显示指定的参数的值就要使用jinfo口令

TODO 研究学习怎么使用jinfo动态修改jvm运行参数

参考
https://www.jianshu.com/p/8d8aef212b25

输出当前 jvm 进程的全部参数和系统属性

```sh
jinfo [进程id]
```

输出当前 jvm 进行的全部的系统属性

```sh
jinfo -sysprops [进程pid]
```

输出jinfo -flag name支持的全部参数，NOTE：-XX:InitialHeapSize对应Xms参数，-XX:MaxHeapSize对应Xmx参数

```sh
jinfo -flags [进程pid]
```

### jmap

参考
https://blog.csdn.net/weixin_42040639/article/details/103771358

打印堆和垃圾回收器信息，不会STW

```sh
jmap -heap [进程id]
```

打印等待回收的对象信息。Number of objects pending for finalization:0 说明当前F-Queue队列中并没有等待Finalizer线程执行finalizer方法的对象。NOTE：不会STW

```sh
jmap -finalizerinfo [进程id]
```



打印堆的对象统计，包括对象数、内存大小等。NOTE：会STW

-histo:live这个命令执行，JVM会先触发gc，然后再统计信息。

```sh
jmap -histo:live [进程id]
```

-histo这个命令执行，不会触发FullGC

```sh
jmap -histo [进程id]
```

打印Java类加载器的智能统计信息，对于每个类加载器而言，对于每个类加载器而言，它的名称，活跃度，地址，父类加载器，它所加载的类的数量和大小都会被打印。此外，包含的字符串数量和大小也会被打印。NOTE: 会STW，在实验环境8g heap使用发现这个命令需要等待很长时间

```sh
jmap -clstats [进程id]
```

dump堆到文件，format指定输出格式，live指明是活着的对象，file指定文件名。NOTE: 会STW

```sh
jmap -dump:live,format=b,file=heapdump.hprof [进程id]
jmap -dump:format=b,file=heapdump.hprof [进程id]
```

### jhat

NOTE：不使用jhat分析heapdump文件，因为当使用jhat解析1g以上的heapdump文件时非常慢，也不能使用MAT，因为如果解析8g的heapdump MAT会报告OOM，使用jprofiler能够解决以上问题

## cpu占用过高分析

参考
https://blog.csdn.net/jwentao01/article/details/115461695

- 分析方面

  业务逻辑线程是否陷入死循环或者代码不够优化导致占用较高cpu

  存在内存泄露导致频繁GC占用较高cpu

- 使用top+jstack分析步骤

  确定占用cpu高的java进程id，-c 显示进程启动的命令行命令

  ```sh
  top -c
  ```

  确定占用cpu高的java线程，-H 表示线程模式，-p 表示指定进程id

  ```sh
  top -H -p [进程id]
  ```

  转换上面获取的线程id为16进制显示，因为jstack需要使用线程的16进制id

  ```sh
  printf "%x\n" [线程id]
  ```

  使用jstack打印指定线程id的调用栈快照，-A 表示grep匹配行向下打印30行

  ```sh
  jstack [进程id] | grep [线程id 16进制] -A 30
  ```



## arthas 使用

### arthas安装和运行

https://arthas.aliyun.com/doc/quick-start.html

### dashboard

https://arthas.aliyun.com/doc/dashboard.html

指定每1秒刷新一次，单位毫秒

```sh
dashboard -i 1000
```

指定总共刷新5次后退出

```sh
dashboard -n 5
```

### thread

https://arthas.aliyun.com/doc/thread.html

列出1000ms内最忙的6个线程栈

```sh
thread -n 6 -i 1000
```

### jvm

https://arthas.aliyun.com/doc/jvm.html

查看当前JVM信息

```sh
jvm
```

### sysprop

查看当前JVM的系统属性(System Property)
https://arthas.aliyun.com/doc/sysprop.html

查看所有属性

```sh
sysprop
```

查看单个属性

```sh
sysprop java.version
```

修改单个属性

```sh
sysprop user.country CN
```

### vmoption

查看，修改VM诊断相关的参数
https://arthas.aliyun.com/doc/vmoption.html

### TODO logger

查看logger信息，更新logger level
https://arthas.aliyun.com/doc/logger.html

### heapdump

dump java heap, 类似jmap命令的heap dump功能。
https://arthas.aliyun.com/doc/heapdump.html

dump到指定文件

```sh
heapdump /tmp/dump.hprof
```

只dump live对象

```sh
heapdump --live /tmp/dump.hprof
```

dump到临时文件

```sh
heapdump
```

### TODO mbean

### TODO ognl

### TODO vmtool

### monitor

对匹配 class-pattern／method-pattern／condition-express的类、方法的调用进行监控。
https://arthas.aliyun.com/doc/monitor.html

对类com.future.demo.performance.ApiArthasController的monitorMethod方法每5秒为一个周期进行监控

```sh
monitor -c 5 com.future.demo.performance.ApiArthasController monitorMethod
```

### watch

让你能方便的观察到指定函数的调用情况。能观察到的范围为：返回值、抛出异常、入参，通过编写 OGNL 表达式进行对应变量的查看。
https://arthas.aliyun.com/doc/watch.html

查看指定的方法入参和返回值，输出结果的属性遍历深度为2

```sh
watch com.future.demo.performance.ArthasService watchMethod "{params,returnObj}" -x 2
```

在方法调用前后打印成员变量watchCount的值

```sh
watch com.future.demo.performance.ArthasService watchMethod "{target.watchCount}" -x 2 -b -s
```

捕捉4次后自动退出命令

```sh
watch com.future.demo.performance.ArthasService watchMethod "{target.watchCount}" -x 2 -b -s -n 4
```

过滤第二个参数等于0才输出ognl表达式

```sh
watch com.future.demo.performance.ArthasService watchMethod "{params,target}" "params[1]==0" -x 2
```

### trace

trace 命令能主动搜索 class-pattern／method-pattern 对应的方法调用路径，渲染和统计整个调用链路上的所有性能开销和追踪调用链路。
https://arthas.aliyun.com/doc/trace.html

追踪指定方法traceMethodLv1调用链路开销

```sh
trace com.future.demo.performance.ArthasService traceMethodLv1
```

### stack

trace 命令能主动搜索 class-pattern／method-pattern 对应的方法调用路径，渲染和统计整个调用链路上的所有性能开销和追踪调用链路。
https://arthas.aliyun.com/doc/trace.html

追踪指定方法traceMethodLv1上游调用链路

```sh
stack com.future.demo.performance.ArthasService traceMethodLv1
```

