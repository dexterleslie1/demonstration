# Apache Commons Exec

>[参考链接](https://blog.csdn.net/u011943534/article/details/120938888)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-commons-exec)



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



## 判断命令是否存在

```java
// region 判断命令是否存在

// 注意：不能直接执行 command 命令，因为它是 sh 内置的命令，所以需要使用 sh 执行此命令
commandLine = new CommandLine("sh");
commandLine.addArgument("-c");
// 使用 command -v 命令判断 libreoffice 命令是否存在
commandLine.addArgument("command -v libreoffice", false);
exec = DefaultExecutor.builder().get();
code = exec.execute(commandLine);
Assert.assertEquals(0, code);

commandLine = new CommandLine("sh");
commandLine.addArgument("-c");
// 使用 command -v 命令判断 libreoffice 命令是否存在
commandLine.addArgument("command -v libreofficex", false);
exec = DefaultExecutor.builder().get();
try {
    exec.execute(commandLine);
    Assert.fail();
} catch (ExecuteException ex) {
    Assert.assertEquals(127, ex.getExitValue());
}

// endregion
```