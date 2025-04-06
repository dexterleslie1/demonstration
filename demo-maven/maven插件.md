# `maven`相关插件使用

## `maven-clean-plugin`插件

`maven-clean-plugin` 是 Maven 的一个插件，它的主要作用是清理项目构建过程中产生的临时文件。在 Maven 项目中，当你执行构建（如编译、打包等）操作时，Maven 会生成一些临时文件或目录，如编译后的字节码文件（`.class` 文件）、打包后的文件（如 `.jar` 或 `.war` 文件）以及 Maven 的目标目录（通常是 `target/` 目录）。这些文件或目录在后续的构建过程中可能会变得过时或不再需要，因此定期清理它们是保持项目整洁和避免潜在问题的好方法。

插件的详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-maven-clean-plugin`

删除`src/main/webapp`目录中的`*.min.js`、`*.min.css`文件

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-clean-plugin</artifactId>
            <version>2.6.1</version>
            <configuration>
                <filesets>
                    <fileset>
                        <directory>${project.basedir}${file.separator}src${file.separator}main${file.separator}webapp</directory>
                        <includes>
                            <include>**${file.separator}*.min.js</include>
                            <include>**${file.separator}app.js</include>
                            <include>**${file.separator}*.min.css</include>
                            <include>**${file.separator}app.css</include>
                        </includes>
                    </fileset>
                </filesets>
            </configuration>
        </plugin>
    </plugins>
</build>
```

执行`mvn clean`时`maven-clean-plugin`会根据配置删除指定文件

```bash
mvn clean
```



## `maven-dependency-plugin`插件

插件的详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-maven-dependency-plugin`，说明：示例在编译后会复制指定的依赖到`target/required-lib`目录下。

复制`jedis`依赖到`target/required-lib`目录下

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.6.1</version>
            <executions>
                <execution>
                    <id>copy</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy</goal>
                    </goals>
                    <configuration>
                        <artifactItems>
                            <artifactItem>
                                <groupId>redis.clients</groupId>
                                <artifactId>jedis</artifactId>
                                <!-- 默认使用项目中声明的依赖版本 -->
                                <!--<version>5.0.2</version>-->
                                <overWrite>true</overWrite>
                                <outputDirectory>${project.build.directory}/required-lib</outputDirectory>
                                <!-- 复制后的文件名称 -->
                                <!--<destFileName>optional-new-name.jar</destFileName>-->
                            </artifactItem>
                        </artifactItems>
                        <outputDirectory>${project.build.directory}/lib</outputDirectory>
                        <overWriteReleases>false</overWriteReleases>
                        <overWriteSnapshots>false</overWriteSnapshots>
                        <overWriteIfNewer>true</overWriteIfNewer>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

触发插件执行

```bash
mvn package
```



## `maven-tomcat-plugin`插件

### `tomcat7-maven-plugin`

>`maven`插件`tomcat7-maven-plugin`的使用`https://blog.csdn.net/xiaojin21cen/article/details/78570254`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-maven-tomcat-plugin/demo-maven-tomcat7-plugin`

运行`tomcat7-maven-plugin`

```bash
mvn tomcat7:run
```

发布`war`

```bash
mvn clean package
```



### `tomcat9 maven`插件

>使用`org.codehaus.cargo:cargo-maven2-plugin`插件

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-maven-tomcat-plugin/demo-maven-tomcat9-plugin`

运行

```bash
mvn package cargo:run
```

发布`war`

```bash
mvn clean package
```



## 自定义插件

### 创建和打包插件

>详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-custom-maven-plugin/demo-my-maven-plugin`

使用 IDEA 创建 maven 插件步骤如下：

点击 New > Project... 功能新建项目，在新建项目对话框中左边导航栏选中 Maven Archetype，项目设置信息如下：

- Name 为 demo-my-maven-plugin
- Location 为默认值
- JDK 选择 1.8
- Catalog 为默认值 Internal
- Archetype 选择 org.apache.maven.archetypes:maven-archetype-quickstart
- Version 默认值为 1.1
- GroupId 为 com.future.demo
- ArtifactId 为 demo-my-maven-plugin
- Version 为 1.0.0

点击 Create 创建项目。

为自定义插件创建 mygoal1

```java
@Mojo(name = "mygoal1", defaultPhase = LifecyclePhase.TEST)
public class MyGoal1Mojo
        extends AbstractMojo {
    @Parameter(property = "myParam", defaultValue = "default value")
    private String myParam;

    public void execute()
            throws MojoExecutionException {
        this.getLog().info("插件mygoal1执行，参数：" + this.myParam);
    }
}
```

为自定义插件创建 mygoal2

```java
@Mojo(name = "mygoal2", defaultPhase = LifecyclePhase.TEST)
public class MyGoal2Mojo
        extends AbstractMojo {
    @Parameter(property = "myParam", defaultValue = "default value")
    private String myParam;

    public void execute()
            throws MojoExecutionException {
        this.getLog().info("插件mygoal2执行，参数：" + this.myParam);
    }
}
```

编译并发布自定义插件到本地 maven 中

```bash
mvn clean install
```



### 引用插件

>详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-custom-maven-plugin/demo-my-maven-plugin-tester`

pom 配置中加入以下依赖：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.future.demo</groupId>
            <artifactId>demo-my-maven-plugin</artifactId>
            <version>1.0.0</version>
            <configuration>
                <myParam>Hello 你好！</myParam>
            </configuration>
            <executions>
                <execution>
                    <!-- test 阶段执行该插件，插件中已经定义默认 phase，在此其实可以不需要重复指定 -->
                    <phase>test</phase>
                    <goals>
                        <!-- 执行插件的 mygoal1 和 mygoal2 -->
                        <goal>mygoal1</goal>
                        <goal>mygoal2</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```



### 插件的生命周期和阶段实验

注意：在示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-custom-maven-plugin/demo-my-maven-plugin-tester`做以下实验

clean 生命周期不会触发调用插件

```bash
mvn clean
```

default 生命周期的 test 阶段会触发调用插件

```bash
mvn test
```

default 生命周期的 deploy 阶段会触发调用插件

```bash
mvn deploy
```



## spring-boot-maven-plugin 插件

>`https://docs.spring.io/spring-boot/docs/3.1.6/maven-plugin/reference/htmlsingle/`

注意：环境变量 $JAVA_HOME 指向的 jdk 和 java -version 使用的 jdk 需要一致，否则会 mvn package 时会报错。



### 以 spring-boot-starter-parent 为 parent

pom 配置文件添加配置如下：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.0</version>
</parent>

<build>
    <finalName>demo</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```



### 不以 spring-boot-starter-parent 为 parent

pom 配置文件添加配置如下：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.4.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <finalName>demo</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <!-- SpringBoot 项目指向的 parent 不是 spring-boot-starter-parent 时，需要显式指定插件执行的目标 repackage -->
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```



### 使用插件启动 Spring Boot 应用

```bash
mvn spring-boot:run
```



## maven-surefire-plugin 插件

### 介绍

maven-surefire-plugin是Maven的一个插件，主要用于执行Java应用程序中的单元测试。以下是关于maven-surefire-plugin的详细介绍：

一、主要功能

1. **执行单元测试**：maven-surefire-plugin能够自动发现并执行项目中的测试类，默认使用JUnit来执行测试，但也可以配置为支持其他测试框架，如TestNG。
2. **报告生成**：该插件提供详细的测试结果报告，包括测试用例的数量、成功/失败/忽略的数量等，这些报告通常生成在项目的target/surefire-reports目录下。
3. **集成测试框架**：maven-surefire-plugin不仅支持JUnit，还通过配置可以支持其他的测试框架，增加了测试的灵活性和兼容性。
4. **定制测试执行**：用户可以通过配置来定制测试的执行方式，例如指定特定的测试类或方法进行执行，或者排除某些测试类和方法。

二、使用方法

1. **添加依赖**：在项目的pom.xml文件中添加maven-surefire-plugin插件的配置。
2. **配置插件**：根据需要对插件进行配置，比如指定要包含或排除的测试类，设置JVM参数等。
3. **执行测试**：通过Maven命令来执行测试，例如使用“mvn test”命令。如果想要跳过测试，可以在命令行中使用“-DskipTests”或“-Dmaven.test.skip=true”参数。

三、配置选项

maven-surefire-plugin提供了多种配置选项，以满足不同的测试需求：

1. **forkCount**：指定在测试过程中JVM fork的数量，多JVM并行可以提升测试执行的性能。
2. **reuseForks**：控制是否重用已经fork出来的JVM实例，以避免每次测试都启动新的JVM。
3. **argLine**：用于向fork出来的JVM添加命令行参数，可以指定JVM参数、系统属性、内存限制等。
4. **parallel**：允许并行执行测试类或测试方法，提高测试执行速度。
5. **includes和excludes**：用于指定哪些测试类应该包含在测试执行中，哪些类应该排除。
6. **runOrder**：用于指定测试类或测试方法的执行顺序。

四、注意事项

1. 在使用maven-surefire-plugin时，应确保项目中已经包含了相应的测试框架依赖，如JUnit或TestNG。
2. 配置插件时，应注意版本兼容性，使用与Maven和测试框架兼容的插件版本。
3. 定制测试执行时，应谨慎选择包含和排除的测试类和方法，以确保测试的完整性和准确性。

综上所述，maven-surefire-plugin是一个功能强大且灵活的Maven插件，能够帮助开发者在构建过程中自动执行单元测试，并生成详细的测试结果报告。通过合理配置和使用该插件，可以提高代码质量和项目稳定性。



### 跳过单元测试

>[参考链接](https://maven.apache.org/surefire/maven-surefire-plugin/examples/skipping-tests.html)

POM 配置如下：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <skipTests>true</skipTests>
    </configuration>
</plugin>
```

或者在使用 mvn package 时跳过单元测试

```bash
mvn package -Dmaven.test.skip
```

