# 安装 Docker

## centOS8 上安装 Docker

**使用 docker 官方 yum 源在线安装最新版本的 docker**

1. 准备一台 centOS8（centOS stream 8 也是兼容的）虚拟机，本次演示使用的 centOS8 系统信息如下：

   ```sh
   [root@iPhone ~]# uname -a
   Linux iPhone 4.18.0-373.el8.x86_64 #1 SMP Tue Mar 22 15:11:47 UTC 2022 x86_64 x86_64 x86_64 GNU/Linux
   ```

2. 添加 docker 官方 yum 源

   ```sh
   # 安装 yum-config-manager
   yum install yum-utils -y
   
   # 添加 docker 官方 yum 源
   yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
   ```

3. 安装 docker

   > `--allowerasing`为了解决 docker 依赖的`containerd-io`包和系统预安装的`runc`包冲突问题，允许`yum install`命令自动删除或替换`runc`包。

   ```sh
   yum install docker-ce -y --allowerasing
   ```

4. 通过查看 docker 版本检查是否成功安装

   ```sh
   [root@iPhone ~]# docker --version
   Docker version 26.1.2, build 211e74b
   ```

5. 编辑 docker 配置 /etc/docker/daemon.json 文件内容如下：

   > `"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]`用于指定 Docker 镜像的加速地址（通常称为镜像仓库的镜像）指向中国科学技术大学的镜像。当 Docker 客户端尝试从 Docker Hub 或其他官方仓库拉取镜像时，如果配置了 `registry-mirrors`，Docker 守护进程会尝试从配置的镜像地址拉取镜像，而不是直接从官方仓库拉取。

   ```json
   {
     "registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]
   }
   ```

6. 使用 systemctl 启动并配置 docker 开机自启动

   > 如果 docker 服务不能正常运行则查看`/var/log/messages`日志文件分析错误原因。

   ```sh
   # 启动 docker 服务
   systemctl start docker
   
   # docker 服务开机自启动
   systemctl enable docker
   
   # 输出 running 状态表示 docker 服务正常运行
   systemctl status docker
   ```

7. 运行 hello-world docker 镜像检查 docker 服务是否正常

   ```sh
   # 输出 Hello from Docker! 表示 docker 服务正常运行
   docker run --rm hello-world
   ```

   