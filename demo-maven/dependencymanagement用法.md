# `dependencyManagement`的用法

> `dependencyManagement` 是 Maven 中一个非常有用的元素，它允许你在父 POM（或 BOM - Bill of Materials）中管理依赖项的版本和配置，而不实际引入这些依赖项到父 POM 的类路径中。这意味着你可以集中控制项目依赖项的版本，确保在整个项目或一组模块中使用相同版本的库。
>
> 例子详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-maven/demo-dependencymanagement)

例子的目录结构如下：

```bash
.
├── module1
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── future
│       │   │           └── demo
│       │   │               └── module1
│       │   │                   └── Module1Util.java
│       │   └── resources
│       └── test
│           ├── java
│           └── resources
└── parent
    └── pom.xml

```

`parent/pom.xml`内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.future.demo</groupId>
    <artifactId>demo-parent</artifactId>
    <!-- 定义父模块类型为pom -->
    <packaging>pom</packaging>
    <version>1.0.0</version>

    <properties>
        <!-- 指定maven-compiler-plugin插件使用的jdk版本 -->
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <modules>
        <!-- 使用相对路径指向子模块 -->
        <module>../module1</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <!-- commons-lang3统一版本管理 -->
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-lang3</artifactId>
                <version>3.14.0</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>
```

`module1/pom.xml`内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.future.demo</groupId>
        <artifactId>demo-parent</artifactId>
        <version>1.0.0</version>
        <!-- 指定parent项目的相对路径，否则编译时报错 -->
        <relativePath>../parent</relativePath>
    </parent>


    <artifactId>module1</artifactId>
    <!-- 指定构件被构建的类型为jar -->
    <packaging>jar</packaging>

    <dependencies>
        <!-- 父级pom.xml使用dependencyManagement管理common-lang3的版本，所以引用时不需要指定版本 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
    </dependencies>

</project>
```

以上`module1/pom.xml`中引用的`org.apache.commons:commons-lang3`不需要指定版本，因为在`parent/pom.xml`中已经使用`dependencyManagement`进行版本管理

切换到`parent`目录编译项目

```bash
mvn package
```

