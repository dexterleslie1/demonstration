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
