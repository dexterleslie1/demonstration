# `jdk`相关工具

## jps

> [参考链接1](https://blog.csdn.net/wisgood/article/details/38942449)
> [参考链接2](https://docs.oracle.com/javase/7/docs/technotes/tools/share/jps.html)

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

## jinfo

>[参考链接1](https://www.jianshu.com/p/8d8aef212b25)

这个命令作用是实时查看和调整虚拟机运行参数。 之前的jps -v口令只能查看到显示指定的参数，如果想要查看未被显示指定的参数的值就要使用jinfo口令

TODO 研究学习怎么使用jinfo动态修改jvm运行参数

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

## jmap

> [参考链接1](https://blog.csdn.net/weixin_42040639/article/details/103771358)

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

## jhat

NOTE：不使用jhat分析heapdump文件，因为当使用jhat解析1g以上的heapdump文件时非常慢，也不能使用MAT，因为如果解析8g的heapdump MAT会报告OOM，使用jprofiler能够解决以上问题