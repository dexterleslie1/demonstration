# `java`命令参数

## `-XX:+CrashOnOutOfMemoryError`和`-XX:+ExitOnOutOfMemoryError`

>注意：如果 crash 文件（例如：hs_err_pid7.log）已经存在，则 JVM 内存溢出崩溃时不会 overwrite 这个文件，所以要确保目录中不存在 hs_err_*.log 文件。
>
>[内存溢出后`jvm`退出](https://stackoverflow.com/questions/19433753/java-heap-dump-shut-down-what-order)

- `-XX:+CrashOnOutOfMemoryError`在`jvm`退出时打印崩溃日志。
- `-XX:+ExitOnOutOfMemoryError`在`jvm`退出时不做任何动作。



## `-Xmx512m`和`-Xms512m`

`-Xmx` 和 `-Xms` 是 Java 虚拟机（JVM）启动时用于设置堆内存大小的两个重要参数。这些参数帮助控制 Java 应用程序可用的最大和初始堆内存量，对于优化应用程序的性能和避免内存溢出错误非常重要。

### -Xmx

`-Xmx` 参数用于指定 Java 虚拟机可以使用的最大堆内存量。如果应用程序尝试使用超过这个限制的堆内存，将会抛出 `OutOfMemoryError` 异常。这个参数的值通常以兆字节（MB）或吉字节（GB）为单位，例如 `-Xmx512m` 表示最大堆内存为 512 兆字节，而 `-Xmx2g` 表示最大堆内存为 2 吉字节。

### -Xms

`-Xms` 参数用于设置 Java 虚拟机启动时堆内存的初始大小。默认情况下，JVM 会在启动时尝试动态地调整堆内存的大小，直到达到 `-Xmx` 指定的最大值。然而，通过设置 `-Xms` 参数，你可以指定 JVM 启动时应该立即分配的堆内存量。这有助于减少垃圾收集器在应用程序启动和达到稳定状态期间的工作量，因为 JVM 不需要再动态地调整堆内存大小。

### 示例

假设你有一个 Java 应用程序，你希望它在启动时就有足够的堆内存来避免后续的频繁垃圾收集，并且你希望限制其最大堆内存使用量以避免消耗过多的系统资源。你可以这样设置 JVM 参数：

```bash
bash复制代码

java -Xms512m -Xmx1024m -jar your-application.jar
```

这个命令会启动 Java 应用程序，并设置其初始堆内存为 512 兆字节，最大堆内存为 1024 兆字节（即 1 吉字节）。

### 注意事项

- 设置 `-Xmx` 和 `-Xms` 时，应考虑到运行 Java 应用程序的计算机的可用内存量。设置过高的值可能会导致系统资源紧张，影响其他应用程序的性能。
- 在某些情况下，如果 `-Xms` 设置的过大，而物理内存不足，可能会导致 JVM 启动失败。
- 堆内存的大小对 Java 应用程序的性能有显著影响，但也需要考虑其他因素，如垃圾收集器的选择和配置。