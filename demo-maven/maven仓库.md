# `Maven`仓库

## 有哪些`Maven`仓库呢？

常用的Maven公共仓库：

1. Maven Central Repository（中央仓库）
   - URL: http://repo1.maven.org/maven2/
   - 这是Maven默认的远程仓库，也是最大的公共Maven仓库之一，包含了大量的开源库文件和插件。
2. JCenter
   - URL: https://jcenter.bintray.com/
   - JCenter是Bintray提供的公共仓库，包含了大量的开源库和框架。虽然JCenter宣布将转为只读模式，但现有的依赖仍然可以从这里下载。
3. Spring Repository
   - URL: http://repo.spring.io/libs-milestone/
   - Spring框架相关的库和插件通常可以在这个仓库中找到。
4. JBoss Repository
   - URL: http://repository.jboss.org/nexus/content/groups/public/
   - JBoss相关的库和插件仓库。
5. Google Maven Repository
   - URL: http://google-maven-repository.googlecode.com/svn/repository/
   - Google提供的Maven仓库，包含了Google项目相关的库和插件。
6. Java.net Repository
   - URL: https://maven.java.net/content/repositories/public/
   - Java.net维护的公共Maven仓库。
7. Maven Repository for Alfresco
   - URL: http://maven.alfresco.com/nexus/content/groups/public/
   - Alfresco项目相关的Maven仓库。
8. Codehaus Repository
   - URL: http://repository.codehaus.org/
   - Codehaus项目相关的Maven仓库。
9. 阿里云Maven公共仓库
   - 阿里云提供了Maven Central、JCenter等常用Maven制品仓库的镜像功能，使得国内用户能够更快速地下载依赖项。
10. 私有仓库（Remote Repository）
    - 是由用户自己搭建的私有仓库，用于存储和共享自己开发的项目构件。
    - 使用私有仓库可以获取到一些不在公共仓库中的构件，满足特定需求。

## 搭建私有仓库

> 选择使用`nexus3`作为私有仓库

`docker-compose.yaml`配置如下：

```yaml
version: '3.3'  
  
services:  
  nexus:  
    image: sonatype/nexus3   
    ports:  
      - "8081:8081"  
    volumes:  
      - data-nexus:/nexus-data  
    environment:  
      - INSTALL4J_ADD_VM_PARAMS=-Xms512m -Xmx512m -XX:MaxDirectMemorySize=512m -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager  
    restart: always

volumes:
  # 使用外部命名卷方式存储nexus数据，避免docker compose down -v错误删除data-nexus卷
  data-nexus:
    external: true
```

手动创建`vol-data-nexus`卷

```bash
docker volume create data-nexus
```

启动`nexus`服务

```bash
docker compose up -d
```

访问`http://localhost:8081`并登录`admin`用户（第一次登录密码在容器路径`/nexus-data/admin.password`中）

获取`admin`密码

```bash
docker compose exec -it nexus cat /nexus-data/admin.password
```

登录`nexus`后，根据提示重置密码和允许匿名访问`Enable anonymous access`

关闭`nexus`服务

```bash
docker compose down -v
```



## 发布自己库到私有`nexus`仓库中

`maven`项目中`pom.xml`加入如下配置：

> `artifact`的`version`为`x.x.x-SNAPSHOT`时会自动发布到`snapshot`仓库（支持同一个版本更新发布），`version`没有`SNAPSHOT`时会自动发布到`release`仓库（不支持同一个版本更新发布）。

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.future.demo</groupId>
	<artifactId>demo-nexus-distribution</artifactId>
	<!-- 版本SNAPSHOT会自动发布到snapshot仓库 -->
	<version>0.0.1-SNAPSHOT</version>
	<!-- 版本没有SNAPSHOT会自动发布到release仓库 -->
	<!--<version>0.0.1</version>-->
	<packaging>jar</packaging>

	<distributionManagement>
		<!-- release仓库的地址 -->
		<repository>
			<id>demo-nexus-deployer</id>
			<url>http://localhost:8081/repository/maven-releases/</url>
		</repository>
		<!-- snapshot仓库的地址 -->
		<snapshotRepository>
			<id>demo-nexus-deployer</id>
			<url>http://localhost:8081/repository/maven-snapshots/</url>
		</snapshotRepository>
	</distributionManagement>
    
    <build>
		<plugins>
			<plugin>
				<!-- 包含此插件后mvn deploy会自动发布源代码 -->
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-source-plugin</artifactId>
				<version>3.2.0</version>
				<executions>
					<execution>
						<id>attach-sources</id>
						<goals>
							<goal>jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
</project>
```

编辑`~/.m2/settings.xml`在`servers`中加入如下内容：

> 下面`server`的`id`对应`pom.xml`中的`repository id`和`snapshotRepository id`

```xml
<settings>
    <servers>
        <server>
            <id>demo-nexus-deployer</id>
            <username>admin</username>
            <password>xxx</password>
        </server>
    </servers>
</settings>
```

发布`artifact`

> 注意：搭建好`nexus`不需要手动创建仓库，使用默认的`maven-releases`、`maven-snapshots`仓库即可

```bash
mvn deploy
```



## 引用私有`nexus`仓库中的库

项目的`pom.xml`配置如下：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	...
	
	<dependencies>
		<dependency>
			<groupId>com.future.demo</groupId>
			<artifactId>demo-nexus-distribution</artifactId>
			<version>0.0.1-SNAPSHOT</version>
			<!--<version>0.0.1</version>-->
		</dependency>
	</dependencies>

	<repositories>
		<repository>
			<id>xxx</id> <!-- 仓库的唯一ID，如果私有仓库不需要身份认证则随便填写xxx -->
			<url>http://localhost:8081/repository/maven-public/</url> <!-- 仓库的URL，注意：后缀需要使用maven-public，因为同时支持snapshot和release依赖下载 -->
			<releases>
				<enabled>true</enabled> <!-- 是否允许从该仓库下载release版本的构件 -->
				<updatePolicy>daily</updatePolicy> <!-- 更新策略，比如always、daily、interval:X（X分钟）等 -->
				<checksumPolicy>fail</checksumPolicy> <!-- 校验和错误策略 -->
			</releases>
			<snapshots>
				<enabled>true</enabled> <!-- 是否允许从该仓库下载snapshot版本的构件 -->
				<updatePolicy>always</updatePolicy> <!-- snapshot版本的更新策略 -->
			</snapshots>
		</repository>
	</repositories>
</project>

```

## 发布自己的库到阿里`Maven`公共仓库