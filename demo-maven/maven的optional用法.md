# `maven`的`optional`用法

> 例子详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-maven/demo-optional)
>
> 例子中`demo-optional-plugin`用于模拟第三方插件，`demo-tester`用于模拟第三方插件调用者。`demo-optional-plugin`中声明`org.apache.commons:commons-lang3`依赖的`optional`为`true`表示此依赖不传递，调用者需要手动引入`org.apache.commons:commons-lang3`依赖否则会报告`NoClassDefFoundError`。

`demo-optional-plugin/pom.xml`内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.future.demo</groupId>
    <artifactId>demo-optional-plugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.14.0</version>
            <!-- 默认的scope值compile -->
            <scope>compile</scope>
            <!--
                控制org.apache.commons:commons-lang3的依赖传递，optional=true时此依赖不会传递
                调用者需要手动引入此依赖，否则报告NoClassDefFoundError
            -->
            <optional>true</optional>
        </dependency>
    </dependencies>

</project>
```

`demo-tester/pom.xml`内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.future.demo</groupId>
    <artifactId>demo-tester</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>

        <!-- 引用com.future.demo:demo-optional-plugin插件 -->
        <dependency>
            <groupId>com.future.demo</groupId>
            <artifactId>demo-optional-plugin</artifactId>
            <version>1.0.0</version>
        </dependency>
        <!--
            因为插件中声明org.apache.commons:commons-lang3依赖为optional=true，
            所以需要手动引入依赖，否则报告NoClassDefFoundError错误
        -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.14.0</version>
        </dependency>
    </dependencies>

</project>
```

测试演示步骤如下：

- 先在本地编译和安装`demo-optional-plugin`

  ```bash
  # 进入demo-optional-plugin目录
  cd demo-optional-plugin
  
  # 编译和安装插件
  mvn package install
  ```

- 使用`IntelliJ IDEA`运行`demo-tester`中的测试