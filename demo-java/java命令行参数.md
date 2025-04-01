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



## `-Xss`

`-Xss` 是 Java 虚拟机（JVM）启动时的一个参数，用于设置每个线程的堆栈大小。堆栈是线程用于存储局部变量和执行环境（如方法调用和返回地址）的内存区域。`-Xss` 参数允许你指定这个内存区域的大小，单位是字节（byte），但通常我们会使用更易于理解的单位，如千字节（KB）或兆字节（MB），通过在数字后加 `k` 或 `m` 来表示。

### 使用示例

- 设置每个线程的堆栈大小为 512KB：

  ```bash
  java -Xss512k YourClassName
  ```
  
- 或者，使用兆字节单位（虽然通常不太常用这么大的值作为线程堆栈大小）：

  ```bash
  java -Xss1m YourClassName
  ```

### 注意事项

1. **性能与内存使用**：增加堆栈大小可以为线程提供更多的执行空间，从而避免在某些极端情况下由于堆栈溢出而导致的 `StackOverflowError`。然而，过大的堆栈大小会消耗更多的内存，可能导致更多的内存溢出（`OutOfMemoryError`），特别是在创建大量线程的应用程序中。
2. **默认值**：JVM 的默认堆栈大小取决于操作系统和JVM的具体实现。在 32 位 JVM 中，默认值可能较小（例如，256KB 或 512KB），而在 64 位 JVM 中，默认值可能更大（例如，1MB）。
3. **平台依赖性**：虽然 `-Xss` 参数在大多数 JVM 实现中都是可用的，但具体的默认值和支持的最大值可能会有所不同。
4. **调整堆栈大小**：调整堆栈大小是优化 JVM 性能时可能需要考虑的一个方面，特别是在处理深度递归调用或大量本地变量的应用程序中。然而，通常建议仅在确实需要时才调整此参数，因为不恰当的调整可能会导致不必要的内存消耗或性能问题。
5. **使用场景**：如果你的应用程序频繁地遇到 `StackOverflowError`，并且确定这是由于堆栈空间不足引起的，那么增加 `-Xss` 参数的值可能是一个解决方案。然而，在大多数情况下，更好的做法是优化代码，减少递归深度或避免在单个方法中分配大量局部变量。



### 找出`-Xss10m`支持的递归深度

- 启动测试中添加`-Xss10m`参数
- 通过测试 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-java-assistant/src/test/java/com/future/demo/XssMaximumRecursionDepthTests.java) 找出`-Xss10m`支持的最大递归深度



### 测试`-Xss`参数

1. 编辑辅助项目 [demo-java-assistant](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-java-assistant)

   ```bash
   mvn package
   ```

2. 运行辅助项目中的`-Xss`测试

   ```bash
   java -Xss11m -jar target/demo.jar xss
   ```

3. 结论

   - 当`-Xss`设置为`-Xss512k`时，会报告`StackOverflowError`错误，说明`-Xss`的大小影响函数调用深度是否报告`StackOverflowError`错误
   - 递归深度越深占用栈内存越多