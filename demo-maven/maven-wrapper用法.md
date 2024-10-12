# `maven wrapper`用法

>[参考]( https://maven.apache.org/wrapper/)

运行以下命令自动设置当前项目的`maven wrapper`，`-Dmaven=3.5.4`指定`maven wrapper`版本为`3.5.4`

```bash
mvn wrapper:wrapper -Dmaven=3.5.4
```

替换`.mvn/wrapper/maven-wrapper.properties`中的`distributionUrl`为`https://bucketxyh.oss-cn-hongkong.aliyuncs.com/maven/apache-maven-3.5.4-bin.zip`，因为在国内下载`https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.5.4/apache-maven-3.5.4-bin.zip`很慢

使用`maven wrapper`编译项目，如果本地没有`maven`则会自动下载并配置`maven`环境，参考此链接指定`maven settings.xml`文件`https://gist.github.com/kbastani/d4b4c92969ec5a22681bb3daa4a80343`

```bash
./mvnw clean package -s .mvn/wrapper/settings.xml
```

