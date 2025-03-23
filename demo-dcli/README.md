# dcli



## 安装

配置`dcli`命令

> 注意：如果提示 curl command not found，ubuntu 系统运行 sudo apt install curl -y，CentOS 系统运行 yum install curl -y

```bash
sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
```

检查`dcli`命令是否配置成功，结果输出`dcli`版本说明配置成功

```bash
sudo dcli -v
```



## 源代码说明

### 目录结构

Go 语言版本的 dcli

```
├── dcli
├── publisher
```

- dcli 目录为 dcli 程序的核心逻辑源代码
- publisher 目录用于发布 dcli 二进制程序到阿里云 OSS，注意：目前使用手动的方式发布二进制程序，所以此目录没有用处了。



## 开发环境搭建

步骤如下：

- 准备 Ubuntu 操作系统

- 参考 <a href="/golang/README.html#ubuntu" target="_blank">链接</a> 安装 Go

- 参考 <a href="/golang/goreleaser使用.html#ubuntu" target="_blank">链接</a> 安装 goreleaser

- 克隆源代码

  ```bash
  git clone https://github.com/dexterleslie1/dcli-go.git
  ```

- 进入 dcli-go/dcli 目录编译 dcli 二进制程序

  ```bash
  ./build.sh
  ```

- 编译成功后二进制程序位于 dcli-go/dcli/dist/dcli_linux_amd64_v1/dcli-linux-x86_64 路径中。

