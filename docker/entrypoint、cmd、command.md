# `entrypoint`、`cmd`、`command`用法

注意：`Dockerfile`中不支持在`entrypoint`指令后编写`shell`脚本代码，只能通过`entrypoint`指定`shell`脚本文件并在脚本中编写代码。

注意：`docker-compose.yaml`文件中支持`entrypoint`换行编写`shell`脚本，但是以下代码不能按照预期执行，所以放弃此方案`shell`脚本编程（使用`entrypoint.sh`方案编程）。

>`https://stackoverflow.com/questions/64465543/docker-compose-entrypoint-with-multiple-or-multiline-commands`

```yaml
version: '3.3'

services:
  test:
    image: redis:7.2.4
    entrypoint:
      - "sh"
      - "-c"
      - |
        for i in $(seq 1 15)
        do
          echo "`date` - hello $i"
        done
```



## `entrypoint`和`cmd`区别

>`https://www.cnblogs.com/Presley-lpc/p/9230271.html`

一、dockerfile中的 CMD
　　　1、每个dockerfile中只能有一个CMD如果有多个那么只执行最后一个。
　　　2、CMD 相当于启动docker时候后面添加的参数看，举个简单例子：docker run -itd --name wohaoshuai docker_image(这个是镜像名称) /bin/bash -c.
　　　　　　a、镜像名称后面跟了一个/bin/bash -c ，其实等价于在dockerfile中的CMD ["/bin/bash","-c"],懂了吧。
　　　　　　b、如果dockerfile中的CMD中有了CMD["/bin/bash","-c"],那么就不用在执行的时候再添加了，如果添加了参数的话那么就相当于要执行你添加的参数，默认的CMD中的参数就无效了。

二、dockerfile中的ENTRYPOINT
　　　1、一个dockerfile中ENTRYPOINT也只能存在一个，若存在多个那么只执行最后一个，你可以理解为开机启动的意思，和CMD有点像，不过还是有区别。
　　　2、举个简单例子：
　　　　　　a、dockerfile中有ENTRYPOINT ["tail","-f","/usr/local/aaa"]这句，那么你启动的时候镜像就执行了这个里面的内容，如果你像上面带参数的话就相当于在这个执行的内容后面再加入参数，懂？
　　　　　　b、如果你和我一样脑子不灵光的话我就再来举个例子吧：
　　　　　　　　如果我们的dockerfile中有a中的这句话然后我们启动我们的docker:
　　　　　　　　　　docker run -itd --name wohaoshuai docker_image(这个是镜像名称) /bin/bash -c。
　　　　　　　　此时就相当于我们启动docker的时候执行了：tail -f /usr/local/aaa /bin/bash -c,这个命令明显就不对嘛，你见过这么牛逼的命令啊？

三、CMD和ENTRYPOINT结合
　　　1、综合一和二我们可以来搞一个正常的CMD与ENTRYPOINT的组合命令嘛，看哥来给你秀一把。
　　　2、dockerfile 内容如下：　　　　　
　　　　FROM daocloud.io/centos:latest
　　　　ADD aaa /usr/local/aaa
　　　　CMD ["-f","/usr/local/aaa"]
　　　　ENTRYPOINT ["tail"]
　　　　制作镜像命令也给你说一下吧：docker build -t my/base  .(这儿有个小点)
　　　　启动命令也给你说一下吧：docker run -itd --name wohaoshuai my/base
　　　3、上面命令启动后就相当于开机执行了tail -f /usr/local/aaa命令



## `entrypoint`



### `entrypoint`指令执行多条命令

> `https://stackoverflow.com/questions/54121031/multiple-commands-on-docker-entrypoint`

`Dockerfile`内容

```dockerfile
FROM ubuntu

ENTRYPOINT [ "/bin/sh", "-c", "date > /tmp/1.txt && cat /tmp/1.txt && sleep infinity" ]

```

`docker-compose.yaml`内容

```yaml
version: "3.0"

services:
  demo-test:
    build:
      context: .
    container_name: demo-test
    image: my-demo-test

```

编译并启动服务

```bash
docker compose build
docker compose up
```



## `entrypoint`和`cmd`综合用法

参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/docker/docker-entrypoint-and-cmd`学习`entrypoint`和`cmd`综合用法。

编译镜像

```bash
docker build -t entrypoint-demo .
```

不指定任何`exec`参数默认传递`CMD`参数到`entrypoint.sh`中，例如本例`echo “.....”`被传递到`entrypoint.sh $@`作为参数执行

```bash
docker run --rm -it --name demo1 entrypoint-demo
```

指定执行`/bin/bash`参数后，`CMD`参数无效，所有直接执行`/bin/bash`

```bash
docker run --rm -it --name demo1 entrypoint-demo /bin/bash
```



## `docker-compose.yaml`文件中使用`entrypoint`和`command`

```yaml
version: '3.3'

services:
  test:
    image: redis:7.2.4
    entrypoint:
      - "sh"
      - "-c"
    command:
      - date && echo "Hello world!"
```



## `docker-compose.yaml`中使用`command`指定参数

>`https://stackoverflow.com/questions/75062680/docker-entrypoint-shell-form-with-parameters`

`Dockerfile`内容如下：

```dockerfile
FROM busybox

ADD entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]
```

`entrypoint.sh`内容如下：

```bash
#!/bin/sh

echo $@
```

`docker-compose.yaml`内容如下：

```yaml
version: '3.3'

services:
  test:
    build:
      context: .
    image: demo
    command:
      - p1

```

编译

```bash
docker compose build
```

运行

```bash
docker compose up
```

