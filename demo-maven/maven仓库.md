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
10. Jitpack
    - URL: https://jitpack.io/
11. 私有仓库（Remote Repository）
    - 是由用户自己搭建的私有仓库，用于存储和共享自己开发的项目构件。
    - 使用私有仓库可以获取到一些不在公共仓库中的构件，满足特定需求。

## 搭建`nexus3`私有仓库

> 选择使用`nexus3`作为私有仓库
>
> [sonatype nexus介绍](https://www.cnblogs.com/aiseek/p/9466247.html)

Sonatype Nexus。它是Sonatype公司的一个产品，叫Nexus，它是Maven的私服。事实上有三种专门的Maven仓库管理软件可以帮助我们创建私服，有Apache的Archiva;JFrog的Artifactory和Sonatype的Nexus。其中Archiva是开源的，Artifactory和Nexus的核心也是开源的。这里我们重点介绍Sonatype公司的Nexus。Sonatype nexus 分为oss和professional版本。

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

`nginx`或`openresty`代理`nexus3`配置文件`nginx.conf`内容如下：

```nginx
worker_processes auto;
worker_rlimit_nofile 65535;

error_log  logs/error.log;
error_log  logs/error.log  notice;
error_log  logs/error.log  info;

events {
    worker_connections  65535;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    gzip on;
    gzip_min_length 1k;
    gzip_buffers 16 64k;
    gzip_http_version 1.1;
    gzip_comp_level 6;
    gzip_types application/json text/plain application/javascript text/css application/xml;
    gzip_vary on;
    server_tokens off;
    access_log off;
    client_max_body_size 50m;

    upstream server_backend_nexus3 {
        # nexus3服务器源地址
        server x.x.x.x:10000;
    }

    server {
        listen 80;
        server_name maven.xxx.net;
        
        location / {
            proxy_http_version 1.1;
            proxy_set_header Connection "";
            proxy_set_header Host $host:80;
            proxy_set_header X-Real-IP        $remote_addr;
            proxy_set_header X-Forwarded-For  $remote_addr;
            proxy_set_header X-NginX-Proxy true;
            proxy_pass http://server_backend_nexus3;
        }
    }
}

```



## 发布构件到私有`nexus`仓库中

`maven`项目中`pom.xml`加入如下配置：

> 构件的`version`为`x.x.x-SNAPSHOT`时会自动发布到`snapshot`仓库（支持同一个版本更新发布），`version`没有`SNAPSHOT`时会自动发布到`release`仓库（不支持同一个版本更新发布）。

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

发布构件

> 注意：搭建好`nexus`不需要手动创建仓库，使用默认的`maven-releases`、`maven-snapshots`仓库即可

```bash
mvn deploy
```



## 引用私有`nexus`仓库中的构件

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

## 发布构件到阿里`Maven`公共仓库

> 暂时使用`jitpack`发布，所以不测试阿里。

## 发布构件到`jitpack`公共仓库

> 访问`jitpack`官网`https://jitpack.io/`
>
> 免费的`jitpack`发布个人构件需要`github`的仓库为`public`类型，因为`jitpack`会自动拉取`github`指定仓库的`tag`并显示在它的界面中，开发者只需要选择相应的`tag`即可触发`jitpack`自动构建。
>
> `jitpack`无法覆盖同一个`tag`更新发布。
>
> 参考 [链接](https://blog.csdn.net/Jul_11th/article/details/79650734)

使用`jitpack`发布构件，构件项目只需要是普通的`maven`项目，详细参考模板项目 [dexterleslie1/maven-jitpack-multiple-module-demo](https://github.com/dexterleslie1/maven-jitpack-multiple-module-demo)

使用`IntelliJ IDEA`（使用你习惯的`git`工具）在项目中创建名为`1.0.11`的`tag`并推送到`github`中。

访问`https://jitpack.io/`打开`jitpack`首页

在`look up`框中输入`https://github.com/dexterleslie1/maven-jitpack-multiple-module-demo`后，点击`look up`按钮

稍后会在界面中显示模板项目中所有`tag`，选择其中没有被构建过的`tag`触发构建（通过点击`tag`上的`log`链接查看详细的构建日志）。

## `jitpack`公共仓库`snapshot`构件调试模式

> 构建`snapshot`构件不需要打标签，只需要选择指定的`github`提交日志即可以临时构建和调试构件。

访问`https://jitpack.io/`打开`jitpack`首页

在`look up`框中输入`https://github.com/dexterleslie1/maven-jitpack-multiple-module-demo`后，点击`look up`按钮

点击`Commits`切换到`github`提交日志显示，选择对应的提交日志触发`jitpack`构建，构件构建完成后把提交对应的`version`填写到项目构件引用的`version`中

## 引用`jitpack`中的构件

访问`https://jitpack.io/`打开`jitpack`首页

在`look up`框中输入`https://github.com/dexterleslie1/maven-jitpack-multiple-module-demo`后，点击`look up`按钮

稍后会在界面中显示模板项目中所有`tag`，点击相应版本的`tag`后的`Get it`按钮后会显示构件相关的`maven`配置信息，例如：

```xml
<!-- 配置jitpack仓库 -->
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<!-- 配置引用的依赖 -->
<dependency>
    <groupId>com.github.dexterleslie1.maven-jitpack-multiple-module-demo</groupId>
    <artifactId>module1</artifactId>
    <version>1.0.11</version>
</dependency>
```

把上面的`maven`配置信息配置到项目的`pom.xml`文件中即可引用`jitpack`发布的构件。

## 发布`android`构件到`jitpack`公共仓库

> todo 抽个时间做测试！

## 发布自定义构件到`maven`中央仓库

> todo 抽个时间做测试！
>
> https://central.sonatype.com/ gmail@Kl(shift)||

## `nexus3`管理

### 仓库类型

nexus3仓库类型主要包括以下几种：

1. group（仓库组）
   - 仓库组没有具体的内容，它会转向其包含的宿主仓库或代理仓库获得实际构件的内容。
   - 它是多个仓库的集合，可以包含多个hosted、proxy、group类型的仓库。
   - 在group中还存在优先级的关系，例如，当多个仓库都包含同一镜像时，会按照配置的优先级顺序进行匹配，一旦匹配成功则不再继续向下匹配。
2. hosted（宿主）
   - 宿主仓库主要是用来存放一些组织内部的构件，或由于版权原因不能放在公共仓库中的构件。
   - 宿主仓库指的是用户自己项目所构建组成的仓库。
3. proxy（代理）
   - 代理仓库用于代理远程仓库，当客户端请求proxy类型仓库时，如果仓库中依赖存在即返回给客户端，如未存在就从proxy配置的远程仓库中拉取依赖返回给客户端，并在proxy仓库中进行缓存。
   - 代理仓库也称为缓存仓库，主要用于代理远程的公共仓库，如Maven中央仓库等。