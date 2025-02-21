# Apache Commons Exec

>`https://blog.csdn.net/u011943534/article/details/120938888`

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-commons-exec`



## maven 项目依赖配置

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-exec</artifactId>
    <version>1.4.0</version>
</dependency>
```



## 基本用法

```java
// region 基本用法

String command = "ping localhost -c 5";
//接收正常结果流
ByteArrayOutputStream susStream = new ByteArrayOutputStream();
//接收异常结果流
ByteArrayOutputStream errStream = new ByteArrayOutputStream();
CommandLine commandLine = CommandLine.parse(command);
DefaultExecutor exec = DefaultExecutor.builder().get();
PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
exec.setStreamHandler(streamHandler);
int code = exec.execute(commandLine);
System.out.println("退出代码: " + code);
System.out.println(susStream.toString("GBK"));
System.out.println(errStream.toString("GBK"));

// endregion
```



## 执行命令时传入环境变量

```java
// region 执行命令时传入环境变量

command = "printenv";
//接收正常结果流
susStream = new ByteArrayOutputStream();
//接收异常结果流
errStream = new ByteArrayOutputStream();
commandLine = CommandLine.parse(command);
exec = DefaultExecutor.builder().get();
streamHandler = new PumpStreamHandler(susStream, errStream);
exec.setStreamHandler(streamHandler);
code = exec.execute(commandLine, new HashMap<String, String>() {{
    this.put("MY_ENV_VAR", "my_value");
}});
System.out.println("退出代码: " + code);
System.out.println(susStream.toString("GBK"));
System.out.println(errStream.toString("GBK"));

// endregion
```