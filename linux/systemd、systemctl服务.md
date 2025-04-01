

# 配置`systemd`、`systemctl`服务

> 注意：修改`xxx.service`文件后执行命令`systemctl daemon-reload`



### 概念

> [参考链接](https://www.jianshu.com/p/b67c0fc7c170)

**systemd** 是 linux 系统中最新的初始化系统(init)，它主要的设计目标是克服 sysvinit 固有的缺点，提高系统的启动速度。systemd 和 ubuntu 的 upstart 是竞争对手，但是时至今日 ubuntu 也采用了 systemd，所以 systemd 在竞争中胜出，大有一统天下的趋势。其实，systemd 的很多概念都来源于苹果 Mac OS 操作系统上的 launchd。

**systemctl** 是一个systemd工具，主要负责控制systemd系统和服务管理器，再换个说法
在systemd管理体系中，被管理的deamon(守护进程)称作unit(单元)，对于单元的管理是通过命令systemctl来进行控制的。unit表示不同类型的systemd对象，通过配置文件进行标识和配置；文件主要包含了系统服务、监听socket、保存的系统快照以及其它与init相关的信息。



### `system`单元文件位置

> 注意：单元配置文件应当放置到`/etc/systemd/system`目录中。

定义 systemd 如何处理单元的文件可以在许多不同的位置找到，每个位置都有不同的优先级和含义。

系统单元文件的副本通常保存在 /lib/systemd/system 目录中。 当软件在系统上安装单元文件时，这是它们默认放置的位置。

存储在此处的单元文件可以在会话期间按需启动和停止。 这将是通用的普通单元文件，通常由上游项目的维护人员编写，应该适用于在其标准实现中部署 systemd 的任何系统。 您不应编辑此目录中的文件。 相反，如果有必要，您应该使用另一个单元文件位置来覆盖该文件，该位置将取代该位置中的文件。

如果您希望修改单元的运行方式，最好的位置是 /etc/systemd/system 目录。 在此目录位置中找到的单元文件优先于文件系统上的任何其他位置。 如果您需要修改系统的单元文件副本，则在该目录中放置替换文件是最安全、最灵活的方法。

如果您只想覆盖系统单元文件中的特定指令，您实际上可以在子目录中提供单元文件片段。 这些将附加或修改系统副本的指令，允许您仅指定要更改的选项。

正确的方法是创建一个以单元文件命名的目录，并在末尾附加 .d。 因此，对于名为 example.service 的单元，可以创建名为 example.service.d 的子目录。 在此目录中，以 .conf 结尾的文件可用于覆盖或扩展系统单元文件的属性。

/run/systemd/system 中还有一个用于运行时单元定义的位置。 在此目录中找到的单元文件的优先级介于 /etc/systemd/system 和 /lib/systemd/system 中的单元文件之间。 此位置中的文件的权重比前一个位置的权重小，但比后者的权重大。

systemd 进程本身使用此位置来动态创建在运行时创建的单元文件。 该目录可用于更改会话期间系统的单元行为。 当服务器重新启动时，在此目录中所做的所有更改都将丢失。

`/usr/lib/systemd/system`存放系统或第三方软件包安装的单元文件。优先级低于`/etc/systemd/system`但高于`/lib/systemd/system`。通常不建议直接修改这里的文件，而是应该复制到`/etc/systemd/system`进行修改。

总结：这些目录用于存放systemd的单元文件，每个目录都有其特定的用途和优先级。`/etc/systemd/system`用于系统管理员的自定义和覆盖，`/usr/lib/systemd/system`用于第三方软件包的单元文件，`/lib/systemd/system`用于系统自带的单元文件，而`/run/systemd/system`用于存放临时的单元文件。



### 用法

#### `After`指令用法

> [参考链接](https://www.baeldung.com/linux/systemd-service-start-order)

确保当前服务在指定服务之后启动。

为了将`service1`配置为在`service2`之后运行，我们可以在`service1`的单元文件中添加一行：

```properties
After=service2.service
```

要在`After`指令中定义多个服务，我们只需将每个服务的名称用空格分隔即可：

```properties
After=service2.service service3.service service4.service
```

一旦所有指定的服务成功运行，当前服务将启动。



#### `Type`指令用法

> [参考链接](https://www.digitalocean.com/community/tutorials/understanding-systemd-units-and-unit-files#anatomy-of-a-unit-file)

这根据服务的进程和守护进程行为对服务进行分类。 这很重要，因为它告诉 systemd 如何正确管理服务并找出其状态。

Type= 指令可以是以下之一：

- **simple**：服务的主进程在起始行指定。 如果未设置 Type= 和 Busname= 指令，但设置了 ExecStart=，则这是默认值。 任何通信都应通过适当类型的第二个单元在单元外部进行处理（如果该单元必须使用套接字进行通信，则通过 .socket 单元进行处理）。 

- **forking**：当服务分叉子进程并几乎立即退出父进程时，使用此服务类型。 这告诉 systemd 即使父进程退出，该进程仍在运行。

使用某些服务类型时可能需要一些附加指令。 例如：

- **PIDFile=**：如果服务类型标记为“forking”，则该指令用于设置应包含应监视的主子进程 ID 号的文件的路径。



#### `PrivateTmp`指令用法

> [参考链接](https://qkxu.github.io/2022/03/16/systemd%E4%B9%8BPrivateTmp.html)
>
> 注意：`arthas`使用`PrivateTmp=True`时会导致`arthas`不能正常运行。

`/tmp`目录以及`/var/tmp`目录所有进程都在公用，不够安全，使用`PrivateTmp`后，进程用于自己的独立的目录以及相应的权限。

关于目录的管理托管于systemd，即当systemd进程启动时会建立相应的目录（目录会在两个地方建立，/tmp以及/var/tmp/下建立两个目录），当通用systemd进程关闭时会删除相应的目录，不用程序单独处理。



#### `Restart=always`、`RestartSec=15`指令用法

>https://gcore.com/learning/how-to-automatically-restart-a-linux-service/

在`service`启动失败后等待`15`秒后自动重启，样例如下：

```properties
[Unit]
Description=Chat System Console service
After=network.target

[Service]
Type=simple
WorkingDirectory=/usr/local/chat/
User=tomcat
ExecStart=java -jar -Xmx512m /usr/local/chat/app-console.war
PrivateTmp=false
Restart=always
RestartSec=15

[Install]
WantedBy=multi-user.target

```



### `centOS7`、`centOS8`配置`systemd`服务

新建文件`/etc/systemd/system/chat-payment.service`内容如下：

```properties
[Unit]
Description=Chat System payment service
After=network.target

[Service]
Type=simple
PIDFile=/var/run/chat-payment.pid
WorkingDirectory=/usr/local/chat-payment
ExecStart=/usr/bin/java -D"eurekaHost=192.168.1.58" -D"paymentDBUser=xxx" -D"paymentDBPassword=xxxx" -jar /usr/local/chat-payment/chat-payment.jar
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

开机自启动

```sh
systemctl enable chat-payment
```

启动服务

```sh
systemctl start chat-payment
```

指定`service`执行用户

```properties
[Unit]
Description=Chat System Zuul image service
After=network.target

[Service]
Type=simple
WorkingDirectory=/usr/local/chat/
User=tomcat
ExecStart=/usr/bin/java -jar /usr/local/chat/app-zuul-image.jar
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

使用`journalctl`查看服务器启动日志，`-f`相当于`tail -f`，提示：`journalctl`使用二进制方式被存储在`/var/log/journal`或`/run/log/journal`目录中

```sh
journalctl -u jmeter-server -f
```

`openresty systemd`配置例子`/etc/systemd/system/openresty.service`内容如下：

```properties
[Unit]
Description=The OpenResty Application Platform
After=syslog.target network-online.target remote-fs.target nss-lookup.target
Wants=network-online.target

[Service]
Type=forking
PIDFile=/usr/local/openresty/nginx/logs/nginx.pid
ExecStartPre=/usr/local/openresty/nginx/sbin/nginx -t
ExecStart=/usr/local/openresty/nginx/sbin/nginx
ExecReload=/bin/kill -s HUP $MAINPID
ExecStop=/bin/kill -s QUIT $MAINPID
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

`gost`服务器`systemd`配置例子`/etc/systemd/system/gost-server.service`内容如下：

```properties
[Unit]
Description=Gost proxy server
After=network.target

[Service]
Type=simple
ExecStart=/usr/local/bin/gost -L https://secretuser:xxx@:30000
ExecReload=/bin/kill -s HUP $MAINPID
ExecStop=/bin/kill -s QUIT $MAINPID
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

`gost`客户端`systemd`配置例子`/etc/systemd/system/gost-client.service`内容如下：

```properties
[Unit]
Description=Gost proxy client
After=network.target

[Service]
Type=simple
ExecStart=/usr/local/bin/gost -L :1080 -F https://secretuser:xxx@xxx.xxx.xxx.xxx:30000
ExecReload=/bin/kill -s HUP $MAINPID
ExecStop=/bin/kill -s QUIT $MAINPID
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```
