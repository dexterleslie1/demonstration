# `arthas`使用

## 参考资料

>[用arthas排查java服务内存占用过高 arthas堆外内存分析](https://blog.51cto.com/u_16099193/9207218)

## `arthas`安装和运行

>[参考链接](https://arthas.aliyun.com/doc/quick-start.html)
>
>注意：如果`java`应用使用`systemctl`运行，请不要把`xxx.service`配置文件中的`PrivateTmp`设置为`true`，否则`arthas`会报告`Unable to open socket file`错误。

1. 启动 math-game

   ```bash
   curl -O https://arthas.aliyun.com/math-game.jar
   java -jar math-game.jar
   ```

   `math-game`是一个简单的程序，每隔一秒生成一个随机数，再执行质因数分解，并打印出分解结果。

   `math-game`源代码：[查看在新窗口打开](https://github.com/alibaba/arthas/blob/master/math-game/src/main/java/demo/MathGame.java)

2. 启动 arthas

   在命令行下面执行（使用和目标进程一致的用户启动，否则可能 attach 失败）：

   ```bash
   curl -O https://arthas.aliyun.com/arthas-boot.jar
   java -jar arthas-boot.jar
   ```

   - 执行该程序的用户需要和目标进程具有相同的权限。比如以`admin`用户来执行：`sudo su admin && java -jar arthas-boot.jar` 或 `sudo -u admin -EH java -jar arthas-boot.jar`。
   - 如果 attach 不上目标进程，可以查看`~/logs/arthas/` 目录下的日志。
   - 如果下载速度比较慢，可以使用 aliyun 的镜像：`java -jar arthas-boot.jar --repo-mirror aliyun --use-http`
   - ``java -jar arthas-boot.jar -h` 打印更多参数信息。

   选择应用 java 进程：

   ```bash
   $ $ java -jar arthas-boot.jar
   * [1]: 35542
     [2]: 71560 math-game.jar
   ```

   `math-game`进程是第 2 个，则输入 2，再输入`回车/enter`。Arthas 会 attach 到目标进程上，并输出日志：

   ```bash
   [INFO] Try to attach process 71560
   [INFO] Attach process 71560 success.
   [INFO] arthas-client connect 127.0.0.1 3658
     ,---.  ,------. ,--------.,--.  ,--.  ,---.   ,---.
    /  O  \ |  .--. ''--.  .--'|  '--'  | /  O  \ '   .-'
   |  .-.  ||  '--'.'   |  |   |  .--.  ||  .-.  |`.  `-.
   |  | |  ||  |\  \    |  |   |  |  |  ||  | |  |.-'    |
   `--' `--'`--' '--'   `--'   `--'  `--'`--' `--'`-----'
   
   
   wiki: https://arthas.aliyun.com/doc
   version: 3.0.5.20181127201536
   pid: 71560
   time: 2018-11-28 19:16:24
   
   $
   
   ```



## 监控`docker`容器中的`java`应用

监控`docker`容器中的`java`应用，需要在`docker`中运行`arthas`并进行监控即可。



## `dashboard`使用

>查看`jvm`线程、内存、`GC`情况
>
>[参考链接](https://arthas.aliyun.com/doc/dashboard.html)

指定每1秒刷新一次，单位毫秒

```bash
dashboard -i 1000
```

指定总共刷新5次后退出

```bash
dashboard -n 5
```



## `thread`使用

>[参考链接](https://arthas.aliyun.com/doc/thread.html)

默认按照 CPU 增量时间降序排列，只显示第一页数据。

```bash
thread
```

显示所有匹配线程信息，有时需要获取全部 JVM 的线程数据进行分析

```bash
thread -all
```

查看指定状态的线程

```bash
thread --state RUNNABLE
```

列出`5000ms`内最忙的6个线程栈

```bash
thread -n 6 -i 5000

[arthas@12006]$ thread -n 6 -i 5000
"cpu负载-0" Id=171 cpuUsage=98.69% deltaTime=4936ms time=50311ms RUNNABLE
    at app//org.springframework.security.crypto.bcrypt.BCrypt.encipher(BCrypt.java:505)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.key(BCrypt.java:595)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.crypt_raw(BCrypt.java:707)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.hashpw(BCrypt.java:793)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.hashpw(BCrypt.java:737)
    at app//org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.encode(BCryptPasswordEncoder.java:108)
    at com.future.demo.performance.CpuService.consume(CpuService.java:64)
    at com.future.demo.performance.CpuService$1.run(CpuService.java:39)
    at java.base@11.0.19/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
    at java.base@11.0.19/java.util.concurrent.FutureTask.run(FutureTask.java:264)
    at java.base@11.0.19/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
    at java.base@11.0.19/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
    at java.base@11.0.19/java.lang.Thread.run(Thread.java:834)


"cpu负载-1" Id=172 cpuUsage=98.04% deltaTime=4903ms time=49181ms RUNNABLE
    at app//org.springframework.security.crypto.bcrypt.BCrypt.encipher(BCrypt.java:505)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.key(BCrypt.java:589)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.crypt_raw(BCrypt.java:707)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.hashpw(BCrypt.java:793)
    at app//org.springframework.security.crypto.bcrypt.BCrypt.hashpw(BCrypt.java:737)
    at app//org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.encode(BCryptPasswordEncoder.java:108)
    at com.future.demo.performance.CpuService.consume(CpuService.java:64)
    at com.future.demo.performance.CpuService$1.run(CpuService.java:39)
    at java.base@11.0.19/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
    at java.base@11.0.19/java.util.concurrent.FutureTask.run(FutureTask.java:264)
    at java.base@11.0.19/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
    at java.base@11.0.19/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
    at java.base@11.0.19/java.lang.Thread.run(Thread.java:834)


"File Watcher" Id=145 cpuUsage=0.12% deltaTime=5ms time=2369ms TIMED_WAITING
    at java.base@11.0.19/java.lang.Thread.sleep(Native Method)
    at app//org.springframework.boot.devtools.filewatch.FileSystemWatcher$Watcher.scan(FileSystemWatcher.java:246)
    at app//org.springframework.boot.devtools.filewatch.FileSystemWatcher$Watcher.run(FileSystemWatcher.java:236)
    at java.base@11.0.19/java.lang.Thread.run(Thread.java:834)


"VM Periodic Task Thread" [Internal] cpuUsage=0.09% deltaTime=4ms time=4492ms


"C1 CompilerThread0" [Internal] cpuUsage=0.08% deltaTime=4ms time=8374ms


"GC Thread#2" [Internal] cpuUsage=0.06% deltaTime=2ms time=1026ms

```



## `jvm`使用

> 显示`jvm`启动参数、垃圾回收器信息、`GC`统计信息、内存使用情况、线程信息。
>
> [参考链接](https://arthas.aliyun.com/doc/jvm.html)

查看当前JVM信息

```bash
jvm
```

THREAD相关

- COUNT：JVM当前活跃的线程数
- DAEMON-COUNT： JVM当前活跃的守护线程数
- PEAK-COUNT：从JVM启动开始曾经活着的最大线程数
- STARTED-COUNT：从JVM启动开始总共启动过的线程次数
- DEADLOCK-COUNT：JVM当前死锁的线程数

## `memory`使用

>[memory命令参考](https://arthas.aliyun.com/doc/memory.html)

查看`jvm`内存使用情况

```bash
memory
```

`memory`命令输出列对应的意义，[参考](https://docs.oracle.com/en/java/javase/11/docs/api/java.management/java/lang/management/MemoryUsage.html)

- used：目前正在使用的内存大小
- total：之前已经申请使用的内存，`committed`内存
- max：可支持最大内存

## `sysprop`使用

> 查看当前`JVM`的系统属性(System Property) [参考链接](https://arthas.aliyun.com/doc/sysprop.html)

查看所有属性

```bash
sysprop
```

查看单个属性

```bash
sysprop java.version
```

修改单个属性

```bash
sysprop user.country CN
```



## `vmoption`使用

> 查看，修改VM诊断相关的参数 [参考链接](https://arthas.aliyun.com/doc/vmoption.html)

## TODO logger

> 查看logger信息，更新logger level [参考链接](https://arthas.aliyun.com/doc/logger.html)

## `heapdump`使用

> dump java heap, 类似jmap命令的heap dump功能。[参考链接](https://arthas.aliyun.com/doc/heapdump.html)

dump到指定文件

```bash
heapdump /tmp/dump.hprof

# 在应用运行的当前目录中创建dump.hprof文件
heapdump dump.hprof
```

只dump live对象

```sh
heapdump --live /tmp/dump.hprof
```

dump到`/tmp/xxx.hprof`

```sh
heapdump
```

## TODO mbean

## TODO ognl

## TODO vmtool

## `monitor`使用

> 对匹配 class-pattern／method-pattern／condition-express的类、方法的调用进行监控。统计每个周期内方法调用次数和平均调用耗时（毫秒数）。[参考链接](https://arthas.aliyun.com/doc/monitor.html)

对类`com.future.demo.performance.ApiArthasController`的`monitorMethod`方法每5秒为一个周期进行监控

```bash
monitor -c 5 com.future.demo.performance.ApiArthasController monitorMethod
```

## `watch`使用

> 让你能方便的观察到指定函数的调用情况。能观察到的范围为：返回值、抛出异常、入参，通过编写`OGNL`表达式进行对应变量的查看。[参考链接](https://arthas.aliyun.com/doc/watch.html)

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

## `trace`使用

> trace 命令能主动搜索 class-pattern／method-pattern 对应的方法调用路径，渲染和统计整个调用链路上的所有性能开销和追踪调用链路。[参考链接](https://arthas.aliyun.com/doc/trace.html)

追踪指定方法traceMethodLv1调用链路开销

```bash
trace com.future.demo.performance.ArthasService traceMethodLv1

[arthas@12006]$ trace com.future.demo.performance.ArthasService traceMethodLv1
Press Q or Ctrl+C to abort.
Affect(class count: 3 , method count: 3) cost in 156 ms, listenerId: 5
`---ts=2024-07-02 09:00:03;thread_name=http-nio-8080-exec-12;id=158;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@20edb5bc
    `---[2903.8607ms] com.future.demo.performance.ArthasService:traceMethodLv1()
        +---[18.10% 525.461178ms ] com.future.demo.performance.ArthasService:sleepRandomly() #31
        `---[81.88% 2377.696287ms ] com.future.demo.performance.ArthasService:traceMethodLv2() #32

`---ts=2024-07-02 09:00:07;thread_name=http-nio-8080-exec-13;id=159;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@20edb5bc
    `---[3543.337142ms] com.future.demo.performance.ArthasService:traceMethodLv1()
        +---[30.99% 1097.937776ms ] com.future.demo.performance.ArthasService:sleepRandomly() #31
        `---[69.00% 2444.977755ms ] com.future.demo.performance.ArthasService:traceMethodLv2() #32

`---ts=2024-07-02 09:00:15;thread_name=http-nio-8080-exec-14;id=160;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@20edb5bc
    `---[3264.46721ms] com.future.demo.performance.ArthasService:traceMethodLv1()
        +---[27.60% 900.833107ms ] com.future.demo.performance.ArthasService:sleepRandomly() #31
        `---[72.40% 2363.455824ms ] com.future.demo.performance.ArthasService:traceMethodLv2() #32
```

## `stack`使用

> `stack`命令能主动搜索 class-pattern／method-pattern 对应的方法完整的上游调用链路。[参考链接](https://arthas.aliyun.com/doc/trace.html)

追踪指定方法`traceMethodLv1`上游调用链路

```sh
stack com.future.demo.performance.ArthasService traceMethodLv1

[arthas@12006]$ stack com.future.demo.performance.ArthasService traceMethodLv1
Press Q or Ctrl+C to abort.
Affect(class count: 3 , method count: 3) cost in 186 ms, listenerId: 6
ts=2024-07-02 09:03:33;thread_name=http-nio-8080-exec-17;id=163;is_daemon=true;priority=5;TCCL=org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader@20edb5bc
    @com.future.demo.performance.ArthasService.traceMethodLv1()
        at com.future.demo.performance.ApiArthasController.trace(ApiArthasController.java:71)
        at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2)
        at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:566)
        at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)
        at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
        at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:879)
        at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793)
        at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
        at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)
        at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)
        at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
        at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)
        at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
        at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:320)
        at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:126)
        at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:90)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:118)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:158)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:116)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:92)
        at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:77)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
        at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)
        at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)
        at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:358)
        at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:271)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
        at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
        at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:373)
        at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
        at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:868)
        at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1594)
        at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
        at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
        at java.lang.Thread.run(Thread.java:834)

```



## `profiler`使用

### 什么是`profiler`？

Arthas的`profiler`命令是一个强大的工具，它在Java应用程序的性能分析和调优中扮演着关键角色。这个命令通过利用`async-profiler`库（或其他类似的性能分析工具，具体取决于Arthas的版本和配置）来收集应用程序运行时的各种性能数据。以下是`arthas profiler`命令的主要作用：

1. **热点分析**：`profiler`可以帮助开发者识别应用程序中的热点代码区域，即执行频率高、消耗资源多的代码段。这有助于开发者优先关注那些对性能影响最大的部分，从而进行针对性的优化。
2. **内存分配分析**：当使用`profiler alloc`等子命令时，`profiler`可以跟踪Java堆中的内存分配情况。这有助于识别内存泄漏、不必要的内存分配和内存使用效率低下的问题。通过分析内存分配热点，开发者可以优化数据结构、减少内存占用和提高内存使用效率。
3. **锁竞争分析**：某些`profiler`子命令（如`profiler lock`）可以跟踪Java中的锁竞争情况。这有助于识别死锁、锁争用和锁饥饿等问题，这些问题可能会导致应用程序的性能下降甚至崩溃。通过分析锁竞争热点，开发者可以优化同步机制、减少锁的使用或改进锁的策略。
4. **CPU性能分析**：使用`profiler cpu`等子命令时，`profiler`可以收集CPU使用情况的数据。这有助于识别哪些代码段占用了最多的CPU时间，从而找到性能瓶颈。通过分析CPU使用热点，开发者可以优化算法、减少不必要的计算或并行化代码以提高性能。
5. **火焰图生成**：`profiler`收集的数据通常以火焰图的形式展示。火焰图是一种性能分析的可视化工具，它通过堆叠的条形图来表示方法的调用关系和调用时间。这种图形化的表示方式使得性能分析更加直观和易于理解。
6. **非侵入式分析**：与传统的性能分析工具相比，`arthas profiler`通常不需要修改应用程序的代码或重新编译。这使得它成为一种非侵入式的性能分析方法，可以在不中断应用程序正常运行的情况下进行性能分析。
7. **动态分析**：`arthas profiler`支持在应用程序运行时动态地启动和停止性能分析。这使得开发者可以在需要时快速地进行性能分析，而无需事先进行复杂的设置或配置。

总之，`arthas profiler`是一个功能强大的性能分析工具，它可以帮助开发者识别和解决Java应用程序中的性能问题。通过热点分析、内存分配分析、锁竞争分析、CPU性能分析以及火焰图的生成等功能，`arthas profiler`为Java应用的性能调优提供了有力的支持。

### `CPU`火焰图

1. 编译并运行[demo-springboot-performance](https://github.com/dexterleslie1/demonstration/tree/master/performance/jvm/demo-springboot-performance)演示项目，注意：需要使用`java -jar demo-springboot-performance.jar -Xmx4g -Xms4g`命令运行

2. 使用`jmeter`打开[cpu负载.jmx](https://github.com/dexterleslie1/demonstration/blob/master/performance/jvm/demo-springboot-performance/cpu%E8%B4%9F%E8%BD%BD.jmx)用于给应用加`cpu`负载

3. 运行`arthas`并选择`demo-springboot-performance`进程

4. 生成`cpu`火焰图

   ```bash
   # 默认是--event cpu，对cpu生成火焰图
   profiler start
   
   # 查看profiler状态
   profiler status
   
   # 查看已经采样的个数
   profiler getSamples
   
   # 停止采样并输出html格式的火焰图
   profiler stop
   ```

5. 使用浏览器打开火焰图对应的`html`，`html`在目录`arthas-output`中

6. 在火焰图中能够直观地看到`com/future/demo/performance/CpuService.consume`最宽表示占用`cpu`时间最多

### 内存分配火焰图

1. 编译并运行[demo-springboot-performance](https://github.com/dexterleslie1/demonstration/tree/master/performance/jvm/demo-springboot-performance)演示项目，注意：需要使用`java -jar demo-springboot-performance.jar -Xmx4g -Xms4g`命令运行

2. 使用`jmeter`打开[memory负载.jmx](https://github.com/dexterleslie1/demonstration/blob/master/performance/jvm/demo-springboot-performance/memory%E8%B4%9F%E8%BD%BD.jmx)用于给应用加内存分配负载

3. 运行`arthas`并选择`demo-springboot-performance`进程

4. 生成`cpu`火焰图

   ```bash
   # 对内存分配生成火焰图
   profiler start --event alloc
   
   # 查看profiler状态
   profiler status
   
   # 查看已经采样的个数
   profiler getSamples
   
   # 停止采样并输出html格式的火焰图
   profiler stop
   ```

5. 使用浏览器打开火焰图对应的`html`，`html`在目录`arthas-output`中

6. 在火焰图中能够直观地看到`com/future/demo/performance/ApiMemoryController.alloc`比较宽表示内存分配比较多

