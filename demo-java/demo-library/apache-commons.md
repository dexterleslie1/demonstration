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



## Commons Compress

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-commons-compress)



### POM 配置

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-compress</artifactId>
    <version>1.24.0</version>
</dependency>
```



### tar+gzip 压缩

>[参考链接](https://stackoverflow.com/questions/13461393/compress-directory-to-tar-gz-with-commons-compress)

```java
public class GzipCompressionTests {
    /**
     * 测试 tar -cvzf x.tar.gz x
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        // x.tar.gz 文件输出流
        FileOutputStream tarGzFileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        // Gzip 算法输出流
        GzipCompressorOutputStream gzipCompressorOutputStream = null;
        // 归档文件输出流
        TarArchiveOutputStream tarArchiveOutputStream = null;
        try {
            String tarGzPath = "/tmp/archive.tar.gz";

            tarGzFileOutputStream = new FileOutputStream(tarGzPath);
            bufferedOutputStream = new BufferedOutputStream(tarGzFileOutputStream);
            gzipCompressorOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
            tarArchiveOutputStream = new TarArchiveOutputStream(gzipCompressorOutputStream);

            // 添加文件归档entry到压缩包
            File file = new File("1.txt");
            String entryName = file.getName();
            // 文件压缩到 a 文件夹
            TarArchiveEntry tarEntry = new TarArchiveEntry(file, "a/" + entryName);
            tarArchiveOutputStream.putArchiveEntry(tarEntry);
            IOUtils.copy(Files.newInputStream(file.toPath()), tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();

            // 添加文件归档entry到压缩包
            file = new File("2.txt");
            entryName = file.getName();
            // 文件压缩到 a 文件夹
            tarEntry = new TarArchiveEntry(file, "a/" + entryName);
            tarArchiveOutputStream.putArchiveEntry(tarEntry);
            IOUtils.copy(Files.newInputStream(file.toPath()), tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } finally {
            IOException ioException = null;
            try {
                // 压缩完毕
                if (tarArchiveOutputStream != null) {
                    tarArchiveOutputStream.finish();
                    tarArchiveOutputStream.close();
                }
            } catch (IOException ex) {
                ioException = ex;
            }

            try {
                if (gzipCompressorOutputStream != null)
                    gzipCompressorOutputStream.close();
            } catch (IOException ex) {
                ioException = ex;
            }

            try {
                if (bufferedOutputStream != null)
                    bufferedOutputStream.close();
            } catch (IOException ex) {
                ioException = ex;
            }

            try {
                if (tarGzFileOutputStream != null)
                    tarGzFileOutputStream.close();
            } catch (IOException ex) {
                ioException = ex;
            }

            if (ioException != null) {
                throw ioException;
            }
        }
    }
}
```



### zip 压缩

>[参考链接](https://blog.csdn.net/wangmx1993328/article/details/103943688)

#### 压缩 zip 文件

```java
public class ZipCompressionTests {
    /**
     * 测试 zip -r x.zip x
     */
    @Test
    public void test() throws IOException {
        // zip 文件名
        String filenameZip = "archive.zip";
        String filepathZip = "/tmp/" + filenameZip;

        // zip 压缩并归档输出流
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        try {
            zipArchiveOutputStream = new ZipArchiveOutputStream(new File(filepathZip));

            // 仅在需要时使用Zip64扩展。这是默认模式，它会在文件大小或条目数量超过ZIP格式的标准限制时自动启用Zip64扩展。
            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);

            // 添加文件归档entry到压缩包
            File file = new File("1.txt");
            String entryName = file.getName();
            // 文件压缩到 a 文件夹
            ZipArchiveEntry entry = new ZipArchiveEntry(file, "a/" + entryName);
            zipArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.copy(Files.newInputStream(file.toPath()), zipArchiveOutputStream);
            zipArchiveOutputStream.closeArchiveEntry();

            // 添加文件归档entry到压缩包
            file = new File("2.txt");
            entryName = file.getName();
            // 文件压缩到 a 文件夹
            entry = new ZipArchiveEntry(file, "a/" + entryName);
            zipArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.copy(Files.newInputStream(file.toPath()), zipArchiveOutputStream);
            zipArchiveOutputStream.closeArchiveEntry();
        } finally {
            // 压缩完毕
            if (zipArchiveOutputStream != null) {
                zipArchiveOutputStream.finish();
                zipArchiveOutputStream.close();
            }
        }
    }
}
```



#### Zip64Mode 的枚举值

在Apache Commons Compress库中，`Zip64Mode`是一个枚举类型，用于指定在处理ZIP文件时是否使用Zip64扩展。Zip64扩展允许ZIP文件处理超过4GB的文件大小和超过65535个条目的限制。以下是关于`Zip64Mode`的详细解释：

一、`Zip64Mode`的枚举值

- **Always**：总是使用Zip64扩展，即使对于不需要它的条目也是如此。这意味着无论文件大小或条目数量如何，都会使用Zip64格式。
- **AsNeeded**：仅在需要时使用Zip64扩展。这是默认模式，它会在文件大小或条目数量超过ZIP格式的标准限制时自动启用Zip64扩展。
- **Never**：从不使用Zip64扩展。这意味着如果文件大小或条目数量超过了ZIP格式的标准限制，将会导致错误或无法正确压缩/解压缩文件。

二、如何使用`Zip64Mode`

在使用Apache Commons Compress库进行ZIP文件压缩或解压缩时，可以通过设置`ZipArchiveOutputStream`或相关类的`setUseZip64`方法来指定`Zip64Mode`。例如，在创建ZIP文件时，可以这样做：

```java
ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(new FileOutputStream(new File("output.zip")));
zipOut.setUseZip64(Zip64Mode.AsNeeded); // 设置使用Zip64模式的策略
// ... 添加文件到ZIP输出流中 ...
zipOut.close();
```

在上面的代码中，`setUseZip64`方法被设置为`Zip64Mode.AsNeeded`，这意味着如果压缩的文件或条目数量超过了ZIP格式的标准限制，将会自动使用Zip64扩展。

三、注意事项

- 在处理大文件或大量文件时，应确保使用的ZIP库和工具支持Zip64扩展，以避免出现因文件大小或条目数量限制而导致的错误。
- 在选择`Zip64Mode`时，应根据具体需求和场景进行选择。如果需要确保兼容性，可以选择`AsNeeded`模式；如果确定所有文件都需要Zip64扩展，可以选择`Always`模式；如果不希望使用Zip64扩展，则可以选择`Never`模式（但需要注意可能的限制和错误）。

综上所述，`Zip64Mode`在Apache Commons Compress库中用于指定是否使用Zip64扩展来处理ZIP文件。正确设置`Zip64Mode`可以确保在处理大文件或大量文件时不会出现错误，并提高兼容性。