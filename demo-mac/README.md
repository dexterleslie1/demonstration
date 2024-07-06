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



## macOS 虚拟机安装

NOTE: 尝试使用 virtualbox 运行 macOS11 和 macOS12 未能够成功，提示“amd-v is not available”错误可能是因为使用amd cpu 虚拟机化时需要特殊配置，没有做实验证明。



### vmware-workstation17 安装 macOS11 和 macOS12

下载 macOS11 和 macOS12 操作系统 iso，https://archive.org/details/macos_iso

上传 macOS11 和 macOS12 操作系统 iso 到 esxi7 存储中

从 https://github.com/DrDonk/unlocker 下载最新版本 unlocker427.zip 并执行其中的 windows/unlock.exe

重启 windows 操作系统后创建虚拟机

针对 AMD cpu 需要编辑虚拟机对应的 xxx.vmx 文件并加入下面内容才能够启动 macOS 虚拟机，否则虚拟机一直停顿在苹果logo界面

> AMD CPU 安装 macOS 需要修改 vmx 文件
> https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/
> https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```

`vmware workstation17`破解序列号

> [序列号参考链接](https://gist.github.com/PurpleVibe32/30a802c3c8ec902e1487024cdea26251)

```
MC60H-DWHD5-H80U9-6V85M-8280D
```





### esxi7 update3 安装 macOS11 和 macOS12

参考

> https://vmscrub.com/installing-macos-12-monterey-on-vmware-esxi-7-update-3/

下载 macOS11 和 macOS12 操作系统 iso，https://archive.org/details/macos_iso

上传 macOS11 和 macOS12 操作系统 iso 到 esxi7 存储中

从 https://github.com/erickdimalanta/esxi-unlocker 下载 esxi-unlocker-master.zip

上传 esxi-unlocker-master.zip 到 esxi7 存储后，ssh exsi7 主机解压 zip

```sh
unzip esxi-unlocker-master.zip
```

设置 esxi-unlocker-master 权限

```sh
chmod 775 -R esxi-unlocker-301/
```

检查是否已经安装过 unlocker，如果没有安装过 unlocker 下面命令会输出 smcPresent = false

```sh
./esxi-smctest.sh
```

安装 unlocker

```sh
./esxi-install.sh
```

重启 esxi7 服务器

```sh
reboot
```

重启成功后再次检查 unlocker 是否成功安装，如果成功安装下面命令会输出 smcPresent = true

```sh
./esxi-smctest.sh
```

针对 AMD cpu 需要编辑虚拟机对应的 xxx.vmx 文件并加入下面内容才能够启动 macOS 虚拟机，否则虚拟机一直停顿在苹果logo界面

> AMD CPU 安装 macOS 需要修改 vmx 文件
> https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/
> https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```

