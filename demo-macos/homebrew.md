## `homebrew`使用

> `homebrew`官网`https://brew.sh/`
>
> `homebrew`介绍和使用`https://www.jianshu.com/p/de6f1d2d37bf`

`homebrew`是一款`Mac OS`平台下的软件包管理工具，拥有安装、卸载、更新、查看、搜索等很多实用的功能。简单的一条指令，就可以实现包管理，而不用你关心各种依赖和文件路径的情况，十分方便快捷。

判断是否已安装`brew`

```bash
which brew
```

安装`brew`，提示：因为网络慢原因安装过程时间比较长，需要耐心等待。

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
```

卸载`brew`

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/uninstall.sh)"
```

查看`brew`版本

```bash
brew --version
```

判断`brew`状态

```bash
brew doctor
```

查看已安装包列表

```bash
brew list
```

查看`git`所有版本

```bash
brew search git
```

安装最新版本`git`

```bash
brew install git
```

卸载`git`

```bash
brew uninstall git
```

升级`brew`

```bash
brew update
```

查看`node`所有版本，`https://stackoverflow.com/questions/43538993/homebrew-list-available-versions-with-new-formulaversion-format`

```bash
brew search node
```

安装指定版本`node`

```bash
brew install node@16
```



### `brew install`慢或者无法解析国外域名解决办法

> `https://blog.csdn.net/meng825/article/details/103929805`

通过环境变量设置`brew install`安装过程中使用的`socks5`代理服务

```bash
export ALL_PROXY=socks5h://localhost:1080
```

删除环境变量

```bash
unset ALL_PROXY
```



### `updating homebrew`卡住解决方案（后来使用配置`github proxy`方式解决）

> `https://developer.aliyun.com/article/634494`

替换`homebrew-core.git`

```bash
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-core.git
brew update
```



### 解决`brew`报错：`Another active Homebrew update process is already in progress`

> `https://blog.csdn.net/MASILEJFOAISEGJIAE/article/details/85253919`

删除`brew`相关`lock`

```bash
rm -rf /usr/local/var/homebrew/locks
```

