# `spring`的`resource`使用

注意：在`jar`发布包中，不能使用`ResourceUtils.getFile()`获取文件，因为它会抛出`FileNotFoundException`，使用`ClassPathResource.getInputStream()`方法获取`InputStream`。

示例的详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-resource`

示例的解析：例子中有`demo-third-party-library`和`demo-tester`两个子项目，`demo-third-party-library`用于模拟第三方库，`demo-tester`作为测试用途的项目用于测试引用`demo-third-party-library`后读取其中的`classpath external.properties`资源，以达到测试证明`ClassPathResource`是否有能力读取`jar`中的`classpath`资源目的。

运行示例的步骤：

>注意：不能从源代码运行此例子。

编译并安装`demo-third-party-library`到本地`maven`

```bash
cd demo-third-party-library 
mvn package install
```

编译`demo-tester`

```bash
cd demo-tester
mvn package
```

运行测试

```bash
cd demo-tester
java -jar target/demo.jar
```



## `ClassPathResource#exists`方法判断`classpath`资源是否存在

```java
ClassPathResource resource = new ClassPathResource("file-none-exists.properties");
Assert.assertFalse(resource.exists());
```



## 使用`InputStream`读取`classpath`资源

```java
// 测试使用InputStream读取classpath资源
// 不抛出异常认为成功读取数据
InputStream inputStream = null;
try {
    resource = new ClassPathResource("file.properties");
    System.out.println("ClassPathResource path: " + resource.getURL().getPath());
    inputStream = resource.getInputStream();
    StreamUtils.copyToByteArray(inputStream);
} finally {
    if(inputStream != null) {
        inputStream.close();
    }
}
```



## 在应用发布为`jar`后，调用`ClassPathResource#getFile`会报告`FileNotFoundException`

```java
resource = new ClassPathResource("file.properties");
Assert.assertTrue(resource.exists());
try {
    // 调用ClassPathResource.getFile()方法会预期抛出FileNotFoundException
    // 当资源是打包在 JAR、WAR、EAR 或其他归档文件内部时，getFile() 方法将无法直接访问它，因为这些资源并不是以文件系统上的独立文件形式存在的。在这种情况下，尝试调用 getFile() 将会失败，因为文件系统中没有实际的文件与之对应。
    resource.getFile();
    Assert.fail("没有抛出预期异常");
} catch (FileNotFoundException ex) {
    // 预期异常
}
```



## `ResourceLoader`的使用

```java
// 测试ResourceLoader
ClassPathResource classPathResource = new ClassPathResource("file.properties");
ResourceLoader resourceLoader = new DefaultResourceLoader();

// 读取classpath资源
Resource resourceL = resourceLoader.getResource("file.properties");
Assert.assertTrue(resourceL instanceof ClassPathResource);

// 读取classpath资源路径前缀使用classpath:
resourceL = resourceLoader.getResource("classpath:file.properties");
Assert.assertTrue(resourceL instanceof ClassPathResource);

// 使用绝对路径读取classpath资源
// absolutePath例子：file:/home/xxx/workspace-git/demonstration/demo-spring-boot/demo-spring-resource/target/demo.jar!/file.properties
String absolutePath = classPathResource.getURL().getPath();
resourceL = resourceLoader.getResource(absolutePath);
Assert.assertTrue(resourceL instanceof FileUrlResource);

// 读取网络的资源
resourceL = resourceLoader.getResource("https://docs.spring.io/spring/docs/4.0.0.M1/spring-framework-reference/pdf/spring-framework-reference.pdf");
Assert.assertTrue(resourceL instanceof UrlResource);
```

