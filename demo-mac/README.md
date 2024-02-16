## 删除macOS隔离文件权限

> https://malcontentboffin.com/2019/12/macOS-Error-bin-bash-bad-interpreter-Operation-not-permitted.html

显示文件隔离属性

```sh
xattr -l my-file.sh
```

删除文件隔离属性

```sh
xattr -d com.apple.quarantine my-file.sh
```

递归删除文件和目录的隔离属性

```sh
xattr -r -d com.apple.quarantine Kap.app/
```



## homebrew或者brew使用

> homebrew官网
> https://brew.sh/
>
> homebrew介绍和使用
> https://www.jianshu.com/p/de6f1d2d37bf
>
> 什么是 brew
> homebrew是一款Mac OS平台下的软件包管理工具，拥有安装、卸载、更新、查看、搜索等很多实用的功能。简单的一条指令，就可以实现包管理，而不用你关心各种依赖和文件路径的情况，十分方便快捷。

判断是否已安装brew

```sh
which brew
```

安装brew

```sh
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
```

卸载brew

```sh
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/uninstall.sh)"
```

查看brew版本

```sh
brew --version
```

判断brew状态

```sh
brew doctor
```

查看已安装包列表

```sh
brew list
```

查看 git 所有版本

```sh
brew search git
```

安装最新版本 git

```sh
brew install git
```

卸载 git

```sh
brew uninstall git
```

升级brew

```sh
brew update
```

查看 node 所有版本，https://stackoverflow.com/questions/43538993/homebrew-list-available-versions-with-new-formulaversion-format

```sh
brew search node
```

安装指定版本 node

```sh
brew install node@16
```



### brew install 慢或者无法解析国外域名解决办法

https://blog.csdn.net/meng825/article/details/103929805

参考 linux/README.md 中使用 ssh 创建 socks5 服务

通过环境变量设置 brew install 安装过程中使用的 socks5 代理服务

```sh
export ALL_PROXY=socks5h://localhost:10080
```

删除环境变量

```sh
unset ALL_PROXY
```



### updating homebrew卡住解决方案（后来使用配置github proxy方式解决）

https://developer.aliyun.com/article/634494

替换homebrew-core.git

```sh
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-core.git
brew update
```



### 解决brew报错：Another active Homebrew update process is already in progress

https://blog.csdn.net/MASILEJFOAISEGJIAE/article/details/85253919

删除 brew 相关 lock

```sh
rm -rf /usr/local/var/homebrew/locks
```





## macOS 远程桌面管理方案

NOTE：没有找到相关资料在 macOS 中安装 rdp 服务。没有尝试内置的远程管理功能(ARD)，因为 windows 没有 ARD 客户端软件。

启用内置的屏幕共享和文件共享功能，其中文件共享功能用于解决 realvnc viewer 无法复制粘贴内容时使用。

下载最新版本的 realvnc viewer 连接到服务器。
