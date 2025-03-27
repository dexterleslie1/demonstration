# Apache Commons



## Commons Exec

>[参考链接](https://blog.csdn.net/u011943534/article/details/120938888)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-commons-exec)



### POM 配置

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-exec</artifactId>
    <version>1.4.0</version>
</dependency>
```



### 基本用法

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



### 执行命令时传入环境变量

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



### 判断命令是否存在

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



## Commons IO

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-commons-io)



### POM 配置

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>
```



### IOUtils

```java
/**
 *
 */
public class IOUtilsTests {
    /**
     *
     */
    @Test
    public void test() throws IOException {
        File file = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String content1 = null;
        try{
            inputStream = IOUtilsTests.class.getClassLoader().getResourceAsStream("1.txt");
            content1 = IOUtils.toString(inputStream, "utf-8");
            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;
            }

            file = File.createTempFile("file", ".tmp");

            inputStream = IOUtilsTests.class.getClassLoader().getResourceAsStream("1.txt");
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;
            }

            if(outputStream!=null) {
                outputStream.close();
                outputStream = null;
            }
        }

        try {
            inputStream = new FileInputStream(file);
            String content2 = IOUtils.toString(inputStream, "utf-8");
            Assert.assertEquals(content1, content2);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;
            }
        }
    }
}
```



### FilenameUtils

```java
public class FilenameUtilsTests {
    @Test
    public void test() {
        String temporaryDirectoryPath = System.getProperty("java.io.tmpdir");
        String uuidStr = UUID.randomUUID().toString();
        String path = temporaryDirectoryPath + File.separator + uuidStr + ".doc";
        // 文件的名称（不包括文件后缀名），例如：xxx
        String baseName = FilenameUtils.getBaseName(path);
        Assert.assertEquals(uuidStr, baseName);

        // 文件的扩展名，例如：doc
        String filenameExtension = FilenameUtils.getExtension(path);
        Assert.assertEquals("doc", filenameExtension);

        // 文件的名称（包括文件后缀名），例如：xxx.doc
        String filename = FilenameUtils.getName(path);
        Assert.assertEquals(uuidStr + ".doc", filename);
        
        // 文件父路径，例如：/tmp/
        String fullPath = FilenameUtils.getFullPath(path);
        Assert.assertEquals("/tmp/", fullPath);
    }
}
```