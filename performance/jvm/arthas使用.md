# `arthas`使用

## `arthas`安装和运行

>[参考链接](https://arthas.aliyun.com/doc/quick-start.html)

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

> [参考链接](https://arthas.aliyun.com/doc/jvm.html)

查看当前JVM信息

```bash
jvm
```



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