# `jdk`相关工具

## `jps`

> [参考链接1](https://blog.csdn.net/wisgood/article/details/38942449)
> [参考链接2](https://docs.oracle.com/javase/7/docs/technotes/tools/share/jps.html)

显示本机所有java进程，`-m`显示传递给`main`方法的参数，`-l`显示`main`方法包的完整路径或者`jar`文件的完整路径，`-v`显示传递给`jvm`的参数

```sh
jps -mlv

# 没有那么多输出，只显示main方法完整包路径
jps -l
```

- 配置支持远程`jps`命令

  启动本地`jstatd`（`rmi`协议）服务，使本地`jvm`可以被`jps`、`visualVM`（`jconsole`连接不了）远程监控
  
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
  
  
  
  启动`jstatd`服务
  
  ```sh
  ./jstatd -J-Djava.security.policy=jstatd.all.policy -J-Djava.rmi.server.hostname=192.168.1.173
  ```
  
  
  
  客户机远程连接`jstatd`服务
  
  ```sh
  jps -mlv rmi://192.168.1.173:19000
  ```
  
  
  
  注意：实质上`jconsole`不能远程连接`jstatd`，使用`visualVM`远程连接`jstatd`后，不能查看`cpu`、`thread`情况，似乎此远程`rmi`方案还不如`jmx`方案，所以暂时放弃不使用此方案远程监控，继续使用`jmx`远程监控方法

## `jinfo`

>[参考链接1](https://www.jianshu.com/p/8d8aef212b25)

这个命令作用是实时查看和调整虚拟机运行参数。 之前的`jps -v`口令只能查看到显示指定的参数，如果想要查看未被显示指定的参数的值就要使用`jinfo`口令

`TODO`研究学习怎么使用`jinfo`动态修改`jvm`运行参数

输出当前`jvm`进程的全部参数和系统属性

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



## `jmap`

> [参考链接1](https://blog.csdn.net/weixin_42040639/article/details/103771358)

打印堆和垃圾回收器信息，不会STW

```sh
jmap -heap [进程id]
```

打印等待回收的对象信息。Number of objects pending for finalization:0 说明当前F-Queue队列中并没有等待Finalizer线程执行finalizer方法的对象。NOTE：不会STW

```sh
jmap -finalizerinfo [进程id]
```



打印堆的对象统计，包括对象数、内存大小等。注意：会STW

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
# jmap -dump:live 命令在Java虚拟机（JVM）环境中用于生成当前Java应用程序的堆内存快照（Heap Dump），但这个快照仅包含存活的对象（即，那些仍然可以从根集合（GC Roots）可达的对象）。这个功能在诊断内存泄漏、分析内存使用情况以及优化内存性能时非常有用。
jmap -dump:live,format=b,file=heapdump.hprof [进程id]

# 包括存活或将要被GC的对象
jmap -dump:format=b,file=heapdump.hprof [进程id]
```



## `jhat`

注意：不使用jhat分析heapdump文件，因为当使用jhat解析1g以上的heapdump文件时非常慢，也不能使用MAT，因为如果解析8g的heapdump MAT会报告OOM，使用jprofiler能够解决以上问题



## `jstat`

### 使用`jstat`查看内存使用和`GC`情况

查看`java`进程`id`

```bash
jps -l
```

显示指定`java`进程`GC`情况

```bash
# [interval] 是可选的，表示每次输出之间的时间间隔（以毫秒为单位）。如果省略，则只输出一次数据。
# [count] 也是可选的，表示输出数据的次数。如果省略，则持续输出直到你手动停止。
# jstat输出以KB为单位
jstat -gc <pid> [interval [count]]

jstat -gc 29683 5000
```

上面命令输出解析：

- **S0C/S1C**：第一个/第二个Survivor空间的大小（KB）。
- **S0U/S1U**：第一个/第二个Survivor空间已使用的空间（KB）。
- **EC**：Eden空间的大小（KB）。
- **EU**：Eden空间已使用的空间（KB）。
- **OC**：Old空间的大小（KB）。
- **OU**：Old空间已使用的空间（KB）。
- **MC**：元空间（Metaspace）的大小（KB），在Java 8及以后版本中取代了永久代（PermGen space）。
- **MU**：元空间已使用的空间（KB）。
- **CCSC**：压缩类空间的大小（KB），这仅在某些JVM实现中可用。
- **CCSU**：压缩类空间已使用的空间（KB）。
- **YGC**：年轻代GC次数。
- **YGCT**：年轻代GC总耗时（秒）。
- **FGC**：Full GC次数。
- **FGCT**：Full GC总耗时（秒）。
- **GCT**：GC总耗时（秒），即YGCT + FGCT。



显示内存使用百分比

```bash
jstat -gcutil 77553
```

`-gcutil`：与`-gc`相似，但显示的是已用空间占总空间的百分比，而不是绝对字节数。这对于快速评估内存使用率和垃圾收集效率非常有用。

上面命令输出解析：

- **S0**：年轻代（Young Generation）中第一个Survivor空间（S0区）当前使用的比例，这里显示为0.00%，表示当前S0区几乎为空，没有被对象占用。
- **S1**：年轻代中第二个Survivor空间（S1区）当前使用的比例，这里显示为12.66%，表示S1区有一定比例的空间被对象占用。
- **E**：年轻代中的Eden空间（Eden区）当前使用的比例，这里显示为27.99%，表明Eden区有一定量的对象，但还没有达到满溢的程度。
- **O**：老年代（Old Generation）当前使用的比例，这里显示为0.00%，表明老年代当前几乎没有被对象占用。
- **M**：元数据区（Metaspace，在Java 8及之后版本中替代了永久代PermGen space）当前使用的比例，这里显示为59.36%，表示元数据区占用了大约59.36%的空间。
- **CCS**：压缩类空间（Compressed Class Space）使用的比例，这里显示为60.99%。在启用了指针压缩（Pointer Compression）的JVM中，这个区域用于存储类的元数据，以减少内存占用。
- **YGC**：年轻代垃圾收集（Young Generation GC）的次数，这里显示为3次，表示自JVM启动以来，年轻代已经进行了3次垃圾收集。
- **YGCT**：年轻代垃圾收集所花费的总时间（以秒为单位），这里显示为0.112秒，表示年轻代垃圾收集总共花费了0.112秒的时间。
- **FGC**：完全垃圾收集（Full GC）的次数，也称为老年代垃圾收集次数，这里显示为0次，表示自JVM启动以来，还没有进行过完全垃圾收集。
- **FGCT**：完全垃圾收集所花费的总时间（以秒为单位），这里显示为0.000秒，因为没有进行过完全垃圾收集，所以时间为0。
- **GCT**：垃圾收集总共花费的时间（以秒为单位），这里是YGCT和FGCT的总和，即0.112秒，因为没有进行过完全垃圾收集，所以GCT等于YGCT。



显示内存各个代的容量

```bash
jstat -gccapacity 77553
```

`-gccapacity`：显示各个内存池的容量和它们当前的使用情况（以字节为单位）。这包括年轻代中的Eden区、两个Survivor区（From和To），以及年老代和永久代（或元空间，取决于JVM版本）。

上面命令输出解析：

- **NGCMN**：年轻代（New Generation）的最小容量（以KB为单位）。这里显示为84992.0 KB，是JVM启动时年轻代可以配置的最小内存大小。
- **NGCMX**：年轻代的最大容量（以KB为单位）。这里显示为1361920.0 KB，是年轻代可以扩展到的最大内存大小。
- **NGC**：当前年轻代的容量（以KB为单位）。这里显示为278528.0 KB，是当前年轻代实际占用的内存大小。
- **S0C** 和 **S1C**：Survivor空间0和Survivor空间1的当前容量（以KB为单位）。这里都显示为10240.0 KB，表明每个Survivor空间都有10240 KB的容量。
- **EC**：Eden区的当前容量（以KB为单位）。这里显示为129024.0 KB，是Eden区当前可用的内存大小。
- **OGCMN**、**OGCMX**、**OGC**、**OC**：这些列分别代表老年代（Old Generation）的最小容量、最大容量、当前容量和当前已使用的容量（均以KB为单位）。老年代用于存放长时间存活的对象。这里OGCMN和OGC都是171008.0 KB，表示老年代的最小和当前容量相同，且OC（老年代当前已使用的容量）也是171008.0 KB，说明老年代已经被完全使用。
- **MCMN**、**MCMX**、**MC**：这些列分别代表元空间（Metaspace，在Java 8及之后版本中）的最小容量、最大容量和当前容量（均以KB为单位）。元空间用于存储类的元数据。这里MCMN是0.0 KB（但实际上JVM会设置一个非零的默认值），MCMX是1056768.0 KB，MC是4864.0 KB，表示当前元空间的使用情况。
- **CCSMN**、**CCSMX**、**CCSC**：这些列与压缩类空间（Compressed Class Space）相关，但在你提供的输出中，CCSMN是0.0 KB，这可能意味着压缩类空间没有被启用或配置。CCSMX是1048576.0 KB（即1 GB），是压缩类空间可以扩展到的最大容量。CCSC是512.0 KB，但考虑到CCSMN是0，这个值可能不表示当前实际使用的压缩类空间大小。
- **YGC** 和 **FGC**：分别表示年轻代垃圾收集（Young GC）和完全垃圾收集（Full GC）的次数。这里YGC是3次，表示已经进行了3次年轻代垃圾收集；FGC是0次，表示还没有进行过完全垃圾收集。



## `jstack`

**jstack命令是Java Development Kit（JDK）自带的一个命令行工具**，它主要用于生成Java应用程序的线程快照，即打印出给定Java进程ID、core文件或远程调试服务的Java堆栈信息。这个工具在诊断Java应用程序的性能问题、死锁问题等方面非常有用。

显示所有线程

```bash
./jstack <pid>
```



## `jcmd`

`jcmd`是`JDK 1.7`及以后版本中引入的一个多功能的命令行工具，它提供了一种简单而强大的方式来管理和监控`Java`进程。

### 查看`jcmd`有哪些命令

```
jcmd pid help
```

### 查看`jdk`版本

```bash
jcmd pid VM.version
```

### 显示进程已运行的时间

```bash
jcmd pid VM.uptime
```

### 查看`jvm`启动参数、启动命令、`classpath`信息

```bash
jcmd pid VM.command_line
```

### 查看`jvm`进程支持的`properties`

```bash
jcmd pid VM.system_properties
```

### 查看`jvm`启动参数（包括指定的和默认的参数）

```bash
jcmd pid VM.flags
```

### 显示所有线程

```bash
jcmd pid Thread.print
```

