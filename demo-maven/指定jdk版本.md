# `maven`设置指定`jdk`版本

> 参考`maven`设置指定`jdk`版本 [链接](https://stackoverflow.com/questions/38882080/specifying-java-version-in-maven-differences-between-properties-and-compiler-p)

## `spring-boot`项目中指定`jdk`版本

```xml
<properties>
	<java.version>1.8</java.version>
</properties>
```

## 普通`maven java`项目中指定`jdk`版本

```xml
<properties>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.source>1.8</maven.compiler.source>
</properties>
```

上面的设置实质上是在设置`maven-compiler-plugin`的配置，`maven-compiler-plugin`和上面的等价设置如下：

```xml
<plugins>
    <plugin>    
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
        </configuration>
    </plugin>
</plugins>
```

