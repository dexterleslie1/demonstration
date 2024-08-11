# `maven`相关插件使用

## `maven-clean-plugin`插件

`maven-clean-plugin` 是 Maven 的一个插件，它的主要作用是清理项目构建过程中产生的临时文件。在 Maven 项目中，当你执行构建（如编译、打包等）操作时，Maven 会生成一些临时文件或目录，如编译后的字节码文件（`.class` 文件）、打包后的文件（如 `.jar` 或 `.war` 文件）以及 Maven 的目标目录（通常是 `target/` 目录）。这些文件或目录在后续的构建过程中可能会变得过时或不再需要，因此定期清理它们是保持项目整洁和避免潜在问题的好方法。

插件的详细用法请参考 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-maven-clean-plugin)

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

插件的详细用法请参考 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-maven/demo-maven-dependency-plugin)，说明：示例在编译后会复制指定的依赖到`target/required-lib`目录下。

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

