## 基本用法

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

## BOM概念

**Maven BOM（Bill of Materials）** 是一个特殊的POM文件，用于**统一管理一组相关依赖的版本**。

### BOM的核心概念

#### 1. **什么是BOM？**
- BOM是一个"物料清单"，定义了整套技术栈中各个组件的兼容版本
- 它本身不包含代码，只包含依赖版本定义
- 类似于产品的"配件清单"，确保所有部件兼容

#### 2. **BOM文件结构示例**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-platform-bom</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <dependencyManagement>
        <dependencies>
            <!-- 定义整套技术栈的兼容版本 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
                <version>2.7.0</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-feign</artifactId>
                <version>3.1.0</version>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>8.0.29</version>
            </dependency>
            <!-- 更多相关依赖... -->
        </dependencies>
    </dependencyManagement>
</project>
```

#### 3. **BOM的使用方式**
**在项目中导入BOM：**

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>my-platform-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**然后使用依赖时无需版本号：**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- 版本从BOM自动获取 -->
    </dependency>
</dependencies>
```

### 常见的BOM示例

#### 1. **Spring Cloud BOM**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2021.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 2. **Spring Boot BOM**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.7.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### BOM的优势

#### 1. **版本一致性**

```xml
<!-- 没有BOM - 容易版本冲突 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-a</artifactId>
    <version>2.2.0</version>  <!-- 可能不兼容 -->
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-b</artifactId>
    <version>3.1.0</version>  <!-- 可能不兼容 -->
</dependency>

<!-- 使用BOM - 自动保持兼容 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-a</artifactId>
    <!-- 版本由BOM统一管理 -->
</dependency>
```

#### 2. **简化依赖管理**
- 无需记忆各个组件的具体版本号
- 升级时只需修改BOM版本
- 减少版本冲突问题

#### 3. **企业级标准**
大型项目和企业中广泛使用BOM来管理技术栈版本。

### 总结
BOM就像一个**版本协调器**，确保整个技术生态系统中所有组件都能和谐工作，是Maven依赖管理的**最佳实践**。
