# `arthas`使用

>用`arthas`排查`java`服务内存占用过高`arthas`堆外内存分析`https://blog.51cto.com/u_16099193/9207218`

测试下面的`arthas`相关命令需要辅助示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-java-assistant`协助。

编译辅助示例

```bash
mvn package
```

运行辅助示例

```bash
java -jar target/demo.jar
```



## `arthas`安装和运行

>参考链接`https://arthas.aliyun.com/doc/quick-start.html`
>
>注意：如果`java`应用使用`systemctl`运行，请不要把`xxx.service`配置文件中的`PrivateTmp`设置为`true`，否则`arthas`会报告`Unable to open socket file`错误。

1. 启动 math-game

   ```bash
   curl -O https://arthas.aliyun.com/math-game.jar
   java -jar math-game.jar
   ```

   `math-game`是一个简单的程序，每隔一秒生成一个随机数，再执行质因数分解，并打印出分解结果。

   `math-game`源代码`https://github.com/alibaba/arthas/blob/master/math-game/src/main/java/demo/MathGame.java`

2. 启动 arthas

   在命令行下面执行（使用和目标进程一致的用户启动，否则可能 attach 失败）：

   ```bash
   curl -O https://arthas.aliyun.com/arthas-boot.jar
   java -jar arthas-boot.jar
   ```

   - 执行该程序的用户需要和目标进程具有相同的权限。比如以`admin`用户来执行：`sudo su admin && java -jar arthas-boot.jar` 或 `sudo -u admin -EH java -jar arthas-boot.jar`。
   - 如果 attach 不上目标进程，可以查看`~/logs/arthas/` 目录下的日志。
   - 如果下载速度比较慢，可以使用`aliyun`的镜像：`java -jar arthas-boot.jar --repo-mirror aliyun --use-http`
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
>参考链接`https://arthas.aliyun.com/doc/dashboard.html`

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



## `monitor`命令

> 对匹配 class-pattern／method-pattern／condition-express的类、方法的调用进行监控。统计每个周期内方法调用次数和平均调用耗时（毫秒数）。参考链接`https://arthas.aliyun.com/doc/monitor.html`

对类`com.future.demo.performance.ApiArthasController`的`monitorMethod`方法每`5`秒为一个周期进行监控

```bash
monitor -c 5 com.future.demo.ArthasController monitorMethod
```

- `-c` 参数表示每`5`秒为一个周期对方法`monitorMethod`进行监控，直到`ctrl+c`终止。

调用接口`http://localhost:8080/api/v1/arthas/monitor`触发方法被调用。



## `watch`命令

> 让你能方便的观察到指定函数的调用情况。能观察到的范围为：返回值、抛出异常、入参，通过编写`OGNL`表达式进行对应变量的查看。参考链接`https://arthas.aliyun.com/doc/watch.html`

注意：下面的实验需要通过调用接口`http://localhost:8080/api/v1/arthas/watch`触发。

观察函数调用返回时的参数、this 对象和返回值

```bash
watch com.future.demo.ArthasService watchMethod "{params,target,returnObj}" -x 2
```

- `"{params,target,returnObj}"` 指定了你对方法监控时感兴趣的内容，这里表示你想查看方法的参数（`params`）和返回值（`returnObj`）。
- `-x 2` 表示在输出结果时，对于参数和返回值的复杂对象，应该展开两层来显示其内部结构。这意味着如果参数或返回值是一个对象，其直接属性会被显示；如果这些属性本身也是对象，那么这些对象的直接属性也会被显示，但不会再深入展开。



查看指定的方法入参和返回值，输出结果的属性遍历深度为`2`

```sh
watch com.future.demo.ArthasService watchMethod "{params,returnObj}" -x 2
```



在方法调用前后打印成员变量`watchCount`的值

```sh
watch com.future.demo.ArthasService watchMethod "{target.watchCount}" -x 2 -b -s
```

- 在**函数调用之前**观察。
- 在**函数返回之后**观察



捕捉`4`次后自动退出命令

```sh
watch com.future.demo.ArthasService watchMethod "{target.watchCount}" -x 2 -b -s -n 4
```



过滤第二个参数等于`0`才输出`ognl`表达式

```sh
watch com.future.demo.ArthasService watchMethod "{params,target}" "params[1]==0" -x 2
```



方法抛出异常时被捕捉

```bash
watch com.future.demo.ArthasService watchMethod "{params,target,returnObj,throwExp}" -e -x 2
```

- `-e`表示抛出异常时才触发
- `express`中，表示异常信息的变量是`throwExp`



按照方法调用耗时过滤

```bash
watch com.future.demo.ArthasService watchMethod '{params, returnObj}' '#cost>200' -x 2
```

- `#cost>200`(单位是`ms`)表示只有当耗时大于`200ms`时才会输出，过滤掉执行时间小于`200ms`的调用



返回值第二个参数为`0`才捕捉

```bash
watch com.future.demo.ArthasService watchMethod "{params,target,returnObj,throwExp}" "returnObj[1]==0" -x 2
```



返回值第一个参数为包含`aa`才捕捉

```bash
watch com.future.demo.ArthasService watchMethod "{params,target,returnObj,throwExp}" 'returnObj[0].contains("aa")' -x 2
```



## `trace`命令

> trace 命令能主动搜索 class-pattern／method-pattern 对应的方法调用路径，渲染和统计整个调用链路上的所有性能开销和追踪调用链路。参考链接`https://arthas.aliyun.com/doc/trace.html`

- 追踪指定方法`traceMethodLv1`调用链路开销

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

  



- 统计指定包下执行时间大于`1`秒的函数

  >根据调用耗时过滤`https://arthas.aliyun.com/doc/trace.html#%E6%A0%B9%E6%8D%AE%E8%B0%83%E7%94%A8%E8%80%97%E6%97%B6%E8%BF%87%E6%BB%A4`

  运行辅助示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-java-assistant`

  访问`http://localhost:8080/api/v1/arthas/trace`触发调用`trace`

  统计命令

  ```bash
  trace com.future.demo.* * '#cost > 1000'
  ```

  



## `stack`命令

> `stack`命令能主动搜索 class-pattern／method-pattern 对应的方法完整的上游调用链路。参考链接`https://arthas.aliyun.com/doc/trace.html`

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



## `sm`命令

Arthas中的`sm`命令是“Search-Method”的简写，该命令用于搜索并展示所有已经加载到JVM中的类的方法信息。以下是关于`sm`命令的详细解释：

**功能**

`sm`命令可以列出指定类（或符合特定模式的类）的所有方法信息，包括方法的名称、参数类型、返回类型等。这对于了解类的功能、接口以及进行代码调试非常有帮助。

**使用方法**

1. 基本语法：

   ```
   sm [class-pattern] [method-pattern]
   ```

   - `class-pattern`：类名表达式匹配，支持通配符（如`*`）和正则表达式（需开启`-E`选项）。
   - `method-pattern`：方法名表达式匹配，同样支持通配符和正则表达式。

2. 常用选项：

   - `-d`：展示每个方法的详细信息，包括方法的签名、参数类型、返回类型等。
   - `-E`：开启正则表达式匹配，默认为通配符匹配。

3. 示例：

   - 展示`java.lang.String`类加载的所有方法：

     ```
     sm java.lang.String
     ```

   - 展示`java.lang.String`类中名为`toString`的方法的详细信息：

     ```
     sm -d java.lang.String toString
     ```

   - 使用正则表达式匹配类名（如匹配所有以`Controller`结尾的类），并展示其方法信息：

     ```
     sm -E .*Controller$
     ```

   - 使用正则表达式匹配类名（如匹配所有在包`com.future`内以`Controller`结尾的类），并展示其方法信息：

     ```
     sm -E com.future.*Controller$
     ```

   - 查看是否存在`com.future.demo.ArthasController#monitorMethod`方法

     ```bash
     sm -d com.future.demo.ArthasController monitorMethod
     ```

     

**注意事项**

- `sm`命令只能看到由当前类所声明的方法，无法看到父类或接口的方法。
- 在使用通配符进行匹配时，需要注意匹配的范围和精度，以避免匹配到过多的类导致信息混乱。
- 当类名或方法名包含特殊字符或空格时，需要使用引号将其括起来。

**应用场景**

- **代码调试**：在调试过程中，可以通过`sm`命令查看某个类的方法信息，以了解该类的功能和接口。
- **性能分析**：在性能分析过程中，可以通过`sm`命令查看某个类的方法调用情况，以找出性能瓶颈或优化点。
- **代码审查**：在代码审查过程中，可以通过`sm`命令查看某个类的方法签名和参数类型等信息，以判断代码是否符合规范或存在潜在问题。

总之，`sm`命令是Arthas中非常实用的一个命令，它可以帮助开发者快速了解类的方法信息，为代码调试、性能分析和代码审查等工作提供有力支持。



## `sc`命令

Arthas中的sc命令是“Search-Class”的简写，主要用于搜索并查看已经加载到JVM中的类信息。以下是关于Arthas sc命令的详细介绍：

**基本用法**

在Arthas的交互式命令行界面中，输入`sc`命令后跟上类名或类名的部分匹配模式，即可搜索出符合条件的类信息。例如：

- `sc java.lang.String`：搜索并显示`java.lang.String`类的信息。
- `sc demo.*`：使用通配符搜索`demo`包下的所有类。

**参数说明**

sc命令支持多个参数，以提供更详细的类信息或进行更复杂的搜索。以下是一些常用的参数：

- `-d`：显示类的详细信息，包括类名、是否是接口、是否是注解、是否是枚举等，以及类的修饰符、注解、实现的接口、继承的父类等信息。
- `-f`：显示类的字段信息。
- `-x`：决定静态变量的遍历深度。例如，`-x 1`会打印出静态属性的第一层属性。
- `-E`：支持正则表达式搜索，可以搜索符合正则表达式的类名。

**示例**

1. **搜索并显示类的详细信息**：

   ```shell
   sc -d demo.MathGame
   ```

   这条命令将搜索`demo.MathGame`类，并显示其详细信息，包括类名、修饰符、注解、实现的接口、继承的父类等。

2. **搜索并显示类的字段信息**：

   ```shell
   sc -d -f demo.MathGame
   ```

   除了显示类的详细信息外，这条命令还将显示`demo.MathGame`类的字段信息。

3. **使用正则表达式搜索类**：

   ```shell
   sc -E ".*Test$"
   ```

   这条命令将搜索所有以`Test`结尾的类名。

**注意事项**

- 在使用sc命令时，需要确保所搜索的类已经加载到JVM中。如果类尚未加载，sc命令将无法搜索到该类。
- sc命令的结果可能受到JVM的类加载器机制的影响。在某些情况下，可能需要指定特定的类加载器来搜索类。

总的来说，Arthas的sc命令是一个强大的工具，可以帮助Java开发者快速搜索并查看已经加载到JVM中的类信息。通过合理使用sc命令及其参数，开发者可以更有效地进行Java应用的在线诊断和调试。



## `jad`命令

Arthas中的`jad`命令是一个强大的工具，它允许开发者反编译已经加载到JVM中的Java类，并查看其源代码。这对于理解第三方库或框架的内部实现，以及调试和诊断Java应用中的问题时非常有用。

**基本用法**

在Arthas的交互式命令行界面中，使用`jad`命令后跟上要反编译的类的全限定名（包括包名和类名），即可查看该类的源代码。例如：

```shell
jad com.example.MyClass
```

这将反编译`com.example.MyClass`类，并在控制台上显示其源代码。

**参数和选项**

`jad`命令支持一些参数和选项，以提供更灵活的反编译功能：

- **类名模式**：`_class-pattern_` 是必填参数，用于指定要反编译的类名。支持通配符匹配，例如 `com.example.*` 可以匹配`com.example`包下的所有类。
- **类加载器**：可以通过 `-c` 选项指定类加载器的哈希码，以选择特定的类加载器。当存在多个类加载器加载了相同名称的类时，这个选项非常有用。
- **正则表达式**：使用 `-E` 选项可以启用正则表达式匹配类名，而不是默认的通配符匹配。
- **源代码格式**：`--source-only` 选项可以让 `jad` 命令仅输出反编译得到的源代码，而不包含类加载器信息等额外元数据。
- **行号**：`--lineNumber [true|false]` 选项可以控制是否在输出的源代码中包含行号。默认为 `true`，如果设置为 `false`，则不显示行号。
- **输出目录**：使用 `-d` 或 `--directory` 选项可以指定一个目录，将反编译得到的源代码保存到该目录中，而不是显示在控制台上。

**示例**

1. **反编译单个类**：

```shell
jad com.example.MyClass
```

1. **使用正则表达式匹配类名**：

```shell
jad -E "com\.example\..*Service$"
```

这将匹配所有以`Service`结尾的，位于`com.example`包及其子包下的类。

1. **指定类加载器**：

```shell
jad -c <classloader-hash> com.example.MyClass
```

你需要先使用Arthas的`sc -d`命令或其他方法来获取类加载器的哈希码。

1. **反编译并保存到文件**：

```shell
jad --source-only com.future.demo.ArthasService > /home/dexterleslie/1.java
```

**注意事项**

- 反编译得到的源代码可能与原始源代码不完全一致，因为反编译过程可能无法完全恢复原始代码的某些部分（如注释、泛型类型参数的具体化等）。
- 在使用`jad`命令时，需要确保所反编译的类已经加载到JVM中。如果类尚未加载，`jad`命令将无法找到该类。
- 由于反编译涉及到对字节码的分析和转换，因此可能会对性能产生一定影响。在生产环境中使用时，请谨慎操作。



## `mc`命令

Arthas（Alibaba Java Diagnostic Tool）是一款面向Java应用的开源诊断工具，它提供了丰富的功能来帮助开发者定位和解决Java应用中的问题。`mc`（Memory Compiler）命令是Arthas中的一个重要功能，它允许开发者在运行时动态编译和执行Java代码。

以下是`mc`命令的一些关键用法和示例：

**基本用法**

```shell
mc -c <className> <source>
```

- `-c`：指定要编译的类名。
- `<source>`：Java源代码，可以是直接写在命令行中的代码片段，也可以是一个文件路径。

**示例**

1. **直接编写代码片段**

```shell
mc -c HelloWorld 'public class HelloWorld { public static void main(String[] args) { System.out.println("Hello, World!"); } }' -e
```

这个命令会在内存中编译一个名为`HelloWorld`的类，并立即执行它的`main`方法。`-e`选项表示执行编译后的类。

1. **从文件中读取源代码**

假设你有一个名为`HelloWorld.java`的文件，内容如下：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

你可以使用以下命令来编译并执行它：

```shell
mc -c HelloWorld /path/to/HelloWorld.java -e
```

同样，`-e`选项表示执行编译后的类。

**注意事项**

- **依赖管理**：`mc`命令只能编译简单的Java类，如果类依赖于其他库或包，你可能需要手动将这些依赖添加到类路径中。
- **安全性**：动态编译和执行代码可能会带来安全风险，特别是在生产环境中。确保你信任并理解正在执行的代码。
- **性能**：虽然`mc`命令在开发和调试阶段非常有用，但在生产环境中频繁使用可能会影响性能。

**高级用法**

- **指定输出目录**：你可以使用`-d`选项来指定编译后的类文件的输出目录。
- **指定类加载器**：使用`-l`选项可以指定用于加载编译后的类的类加载器。

```shell
mc -c ArthasService /home/dexterleslie/1.java -d /home/dexterleslie -l <classLoaderHash>
```

`<classLoaderHash>`是目标类加载器的哈希值，你可以使用Arthas的`sc`（Search Class）命令来查找类加载器的哈希值。

**总结**

`mc`命令是Arthas中非常强大的一个功能，它允许开发者在运行时动态编译和执行Java代码，这对于快速调试和验证代码非常有帮助。然而，在使用时需要注意安全性和性能问题，特别是在生产环境中。



## 在线热更新`class`

注意：不知道什么原因没有成功加载新的`class`到`jvm`中。

获取`classloader hash`值

```bash
sc -d com.future.demo.ArthasService
```

反编译输出源码到`ArthasService.java`文件

```bash
jad --source-only com.future.demo.ArthasService > /home/dexterleslie/ArthasService.java
```

手动修改`ArthasService.java`源码

通过`ArthasService.java`源码编译`ArthasService.class`

```bash
mc -c 31cefde0 /home/dexterleslie/ArthasService.java -d /home/dexterleslie
```

- `-c 31cefde0`是命令`sc -d com.future.demo.ArthasService`获取的`classloader hash`值

重新加载`ArthasService.class`字节码到`jvm`

```bash
redefine -c 31cefde0 /home/dexterleslie/com/future/demo/ArthasService.class
```

查看方法逻辑是否被修改

```bash
watch com.future.demo.ArthasService watchMethod '{params,target,returnObj}' -x 2 -b -s
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

