## `centOS8`上安装`Docker`



### 使用`dcli`安装

<a href="/dcli/README.html#安装" target="_blank">安装并配置 dcli</a>

使用 dcli 根据提示安装

```bash
sudo dcli docker install
```

重新启动操作系统



### 使用`docker`官方`yum`源在线安装最新版本

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



## `Ubuntu 24.04`上安装`Docker 29.10.1`

> Docker 官方支持 Ubuntu Noble 24.04（LTS），对应包版本：`5:29.10.1-1~ubuntu.24.04~noble`。参考：[Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/)

1. 卸载可能冲突的旧包（若未安装过可跳过）

   ```sh
   sudo apt-get remove -y docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc
   ```

2. 安装依赖并添加 Docker 官方 GPG 密钥与 apt 源

   ```sh
   sudo apt-get update
   sudo apt-get install -y ca-certificates curl
   sudo install -m 0755 -d /etc/apt/keyrings
   sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
   sudo chmod a+r /etc/apt/keyrings/docker.asc

   echo \
     "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
     $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
     sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

   sudo apt-get update
   ```

   > 若下载 GPG 出现 `curl: (35) Recv failure: Connection reset by peer`（`ping download.docker.com` 可能仍通，但 HTTPS 不稳定），可改为**手动下载 gpg 再配置**：
   >
   > 1. 在能正常访问的电脑浏览器打开 https://download.docker.com/linux/ubuntu/gpg ，另存为文件 `docker.asc`（约 3~4KB）。
   > 2. 把 `docker.asc` 拷到目标机，例如放到 `/tmp/docker.asc`（可用 `scp`）。
   > 3. 在目标机执行：
   >
   > ```sh
   > sudo install -m 0755 -d /etc/apt/keyrings
   > sudo install -m 0644 /tmp/docker.asc /etc/apt/keyrings/docker.asc
   > sudo chmod a+r /etc/apt/keyrings/docker.asc
   > ls -l /etc/apt/keyrings/docker.asc
   > ```
   >
   > 确认文件存在后，继续执行上面的 `echo ... docker.list` 与 `sudo apt-get update` 即可。

3. 安装指定版本 `Docker 29.10.1`（含 CLI、containerd、Buildx、Compose 插件）

   ```sh
   VERSION_STRING=5:29.10.1-1~ubuntu.24.04~noble
   sudo apt-get install -y \
     docker-ce=$VERSION_STRING \
     docker-ce-cli=$VERSION_STRING \
     containerd.io \
     docker-buildx-plugin \
     docker-compose-plugin
   ```

4. 通过查看 docker 版本检查是否成功安装

   ```sh
   docker --version
   # 期望输出类似：Docker version 29.10.1, build ...
   ```

5. 编辑 docker 配置 `/etc/docker/daemon.json`（按需配置镜像加速）

   ```json
   {
     "registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]
   }
   ```

6. 启动并配置 docker 开机自启动

   ```sh
   sudo systemctl enable --now docker
   sudo systemctl status docker
   ```

7. 运行 hello-world 检查 docker 服务是否正常

   ```sh
   sudo docker run --rm hello-world
   ```

> 若希望当前用户无需 `sudo` 即可使用 docker，可将用户加入 `docker` 组后重新登录：`sudo usermod -aG docker $USER`



## `windows`上安装`docker`

### `windows11`上安装`docker`

windows11 版本 23H2（OS 内部版本 22631.2861，ISO 镜像：Win11_23H2_Chinese_Simplified_x64v2.iso）专业工作站版，Docker Desktop 4.35.1

- windows11 启用 hyper-v 特性后重启系统，参考本站[链接](/windows/README.html#启用hyper-v特性)
- 安装 Docker Desktop 后即可使用（安装时取消勾选`Use WSL 2 instead of Hyper-V(recommended)`），访问 https://docs.docker.com/desktop/setup/install/windows-install/ 下载4.35.1版本的 Docker Desktop。

启用`host networking`：导航到`Settings` > `Resources` > `Network`功能，勾选`Enable host networking`选项后点击`Apply & restart`按钮即可。

>说明：否则不能使用`network_mode: host`模式。4.35.1版本Docker Desktop才支持此特性（不能下载最新的Docker Desktop，否则IntelliJ IDEA 2023.2.5不能连接本地Docker）

测试Docker服务是否正常运行

```sh
docker pull ubuntu
docker run --rm -it ubuntu bash
# 在容器内运行env命令查看所有环境变量
$ env
```

### Windows Server2016上安装Docker

尝试在Windows Server2016上面安装Docker Desktop，但是报告下面错误：

```
Docker Desktop 4.35.1

Installation failed. One prerequisite is not fulfilled.

We've detected that you have an incompatible version of Windows.

Docker Desktop requires Windows 10 Pro/Enterprise/Home version 19044 or above.

To continue with the installation, upgrade Windows to a supported version and then re-run the installer.
```




## 启用`docker daemon`的调试模式

>[参考链接](https://platform9.com/kb/kubernetes/enable-debug-logging-for-docker-daemon)

编辑`/etc/docker/daemon.json`加入如下配置

```json
"debug": true
```

重启`docker daemon`

```bash
systemctl restart docker
```

执行`docker`或`docker compose`相关命令

```bash
docker run --rm hello-world

docker pull nginx
```

查看`docker daemon`日志

```bash
journalctl -f -u docker
```



## 迁移`Docker`数据目录

复制数据目录

```bash
cp -rp /var/lib/docker /data/data-docker
```

编辑 /etc/docker/daemon.json 添加如下配置

```json
"data-root": "/data/data-docker"
```

