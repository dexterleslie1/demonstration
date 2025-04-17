# git命令相关知识

## 概念

> HEAD：是指向当前分支的最新提交的指针，可以在任意分支进行切换

## git reset

> https://blog.csdn.net/zts_zts/article/details/115220786

```
# NOTE: git reset 时候需要指定重置到的git log hash id

# git reset --mixed(默认)：回退版本后，之后提交的文件或代码在暂存区不跟踪了，转移到工作区，需要用add命令添加跟踪，然后再commit，用于未push场景
# NOTE: 没有写命令演示

# git reset --soft: 回退版本后，之后的代码或文件转移到暂存区继续跟踪，等待commit，用于未push场景
# NOTE: 没有写命令演示

# git reset --hard: 回退版本后，之后添加的代码或文件文件全部清除，包括你编辑器里新写的代码也删除，用于已push场景
# https://blog.csdn.net/weixin_44709394/article/details/120725395

git reset --hard [提交日志hash]

# 强制推送到远程，会把远程提交日志删除
git push -f
```



## git revert

> git revert [提交日志hash]：逆向指定提交的操作，起到撤销提交的效果



## git reset和git revert区别

> https://baijiahao.baidu.com/s?id=1714298482780121972&wfr=spider&for=pc



## git status

设置git status命令显示中文

```shell
git config --global core.quotepath false
```

无论哪个子目录都显示整个项目的状态

```shell
git status
```

显示当前所在目录的状态

```shell
git status .
```

显示当前所在目录的状态包括untracked文件，可参考 https://michaelheap.com/git-status-untracked/

```shell
git status -u .
```



## git lfs命令使用

### ubuntu安装git-lfs

```shell
sudo apt install git-lfs
```

### 使用

```
# pull指定文件，参考https://sabicalija.github.io/git-lfs-intro/
sudo git lfs pull --include="xxx.pdf"
```



## git clean

### 参考资料

How to remove files that are listed in the .gitignore but still on the repository?
https://stackoverflow.com/questions/13541615/how-to-remove-files-that-are-listed-in-the-gitignore-but-still-on-the-repositor#:~:text=As%20the%20files%20in%20.,clean%20%2Dxdf%20to%20execute%20it.

### 作用

用于删除被 .gitignore 但依旧在远程仓库存在的文件

### 相关操作

只显示将要删除的文件或者目录，不执行实际删除，-x 删除被忽略的文件，-d 删除被忽略的目录，-n 等价为 --dry-run 表示模拟输出不实际执行

```shell
git clean -xdn
```

执行实际删除，-f 强制执行

```shell
git clean -xdf
```



## git stash

> https://www.cnblogs.com/zndxall/archive/2018/09/04/9586088.html
> https://stackoverflow.com/questions/1910082/git-stash-apply-version

显示 stash 列表

```sh
git stash list
```

使用最近的提交日志内容作为暂存标识暂存修改

```sh
git stash
```

指定暂存的名称

```sh
git stash save "test1"
```

应用最近一次暂存

```sh
git stash apply
```

应用指定的暂存，0为暂存的索引

```sh
git stash apply 0
```



## git clone、pull、push时指定用户名和密码

> https://stackoverflow.com/questions/11506124/how-to-enter-command-with-password-for-git-pull

```
# 克隆仓库
git clone http://root:token-string-here123456@localhost/root/demo-devops.git

# stage所有文件
git add .
# 提交
git commit -m 'init commit'

# 推送提交到远程
git push http://root:token-string-here123456@localhost/root/demo-devops.git --all

# 拉取代码
git pull http://root:token-string-here123456@localhost/root/demo-devops.git
```



## git使用http、socks5代理加速

> https://gist.github.com/goncha/4591538

```
# 全局方式配置http.proxy
git config --global http.proxy 'socks5h://192.168.3.29:10080'
git config --global https.proxy 'socks5h://192.168.3.29:10080'
git config --global --unset http.proxy
git config --global --unset https.proxy

# 显示config配置
https://stackoverflow.com/questions/12254076/how-do-i-show-my-global-git-configuration
git config --list

# 每次clone指定http.proxy
# https://stackoverflow.com/questions/15227130/using-a-socks-proxy-with-git-for-the-http-transport
git clone https://github.com/cmu-db/benchbase.git --config 'http.proxy=socks5://192.168.1.55:1080'
```



## fork仓库

### 参考资料

https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo

### NOTE

目前工作中暂时没有用到此特性，所有不深入研究。



## git 显示和替换远程地址

显示远程地址

```sh
git remote -v
```

替换远程地址

```sh
git remote set-url origin https://github.com/username/new-repo.git
```



## GUI 客户端



### GitExtensions

>支持 Windows。

#### 介绍

**GitExtensions** 是一个功能强大且开源的 Git 图形用户界面（GUI）工具，专为 Windows 平台设计，旨在简化 Git 的使用并提升开发效率。它提供了直观的界面和丰富的功能，使开发者能够更轻松地管理 Git 仓库、执行版本控制操作以及与团队协作。

------

**主要特点**

1. **直观的图形界面**
   GitExtensions 提供了一个用户友好的界面，使开发者可以通过图形化操作完成常见的 Git 任务，如提交、推送、拉取、分支管理等，而无需记忆复杂的命令行指令。
2. **与 Windows 资源管理器集成**
   它集成了 Windows 资源管理器，允许用户直接在文件资源管理器中查看 Git 仓库的状态，执行 Git 操作，而无需打开单独的应用程序。
3. **与 Visual Studio 集成**
   GitExtensions 提供了 Visual Studio 插件，使开发者可以直接在 Visual Studio 中使用 Git 功能，无需切换工具，从而提高开发效率。
4. **内置差异和合并工具**
   工具内置了差异（diff）和合并（merge）功能，用户可以直观地查看文件的更改，并轻松解决合并冲突。
5. **子模块支持**
   GitExtensions 支持 Git 子模块，方便开发者管理和更新项目中的子模块，适合复杂项目的开发。
6. **历史记录查看**
   提供了详细的提交历史记录查看功能，包括图形化的提交树（commit tree），方便用户浏览和管理项目的历史记录。
7. **多语言支持**
   支持多种语言界面，方便全球开发者使用。
8. **脚本和插件扩展**
   支持脚本和插件扩展，用户可以根据自己的需求定制和扩展功能。

------

**适用场景**

- **新手开发者**：对于不熟悉 Git 命令行的开发者，GitExtensions 提供了直观的界面，降低了学习曲线。
- **团队协作**：通过图形化界面，团队成员可以更方便地协作开发，管理分支和合并代码。
- **复杂项目管理**：支持子模块和详细的历史记录查看，适合管理大型和复杂的项目。
- **集成开发环境（IDE）用户**：与 Visual Studio 的集成使开发者无需离开 IDE 即可使用 Git 功能。

------

**安装与使用**

1. 安装
   - 下载 GitExtensions 的安装包（可从 [GitHub Releases](https://github.com/gitextensions/gitextensions/releases) 获取）。
   - 运行安装程序，按照提示完成安装。
   - 安装过程中可以选择安装与 Visual Studio 的集成插件。
2. 基本使用
   - **克隆仓库**：通过 GitExtensions 可以轻松克隆远程仓库到本地。
   - **提交更改**：在界面中查看修改的文件，选择要提交的文件，填写提交信息并提交。
   - **推送和拉取**：将本地更改推送到远程仓库，或从远程仓库拉取最新更改。
   - **分支管理**：创建、切换、合并和删除分支。
   - **解决冲突**：在合并时，如果发生冲突，GitExtensions 会提示冲突的文件，用户可以在界面中解决冲突。

------

**优势**

- **开源免费**：GitExtensions 是开源软件，用户可以免费使用，并且可以根据需要自定义和扩展功能。
- **跨平台支持**：虽然主要针对 Windows，但通过 Mono 等工具，GitExtensions 也可以在 Linux 和 macOS 上运行。
- **社区支持**：作为开源项目，GitExtensions 拥有活跃的社区，用户可以获取帮助、报告问题或贡献代码。

------

**总结**

GitExtensions 是一个功能全面、易于使用的 Git 图形用户界面工具，特别适合 Windows 平台的开发者。它通过直观的界面和强大的功能，简化了 Git 的使用，提高了开发效率，是团队协作和复杂项目管理的理想选择。无论是新手还是有经验的开发者，都能从 GitExtensions 中受益。



#### 安装

在 Windows 11 中安装

下载最新版本的 [GitExtensions](https://github.com/gitextensions/gitextensions/releases)，例如：GitExtensions-x64-5.2.1.18061-0d74cfdc3.msi，根据 msi 安装程序提示安装 GitExtensions

下载最新版本 Git 客户端 [链接](https://git-scm.com/downloads/win)，例如：Git-2.49.0-64-bit.exe，根据 exe 安装程序提示安装 Git 客户端

双击 GitExtensions 即可运行。



## 多平台换行符问题（LF or CRLF）

>[参考链接](http://kuanghy.github.io/2017/03/19/git-lf-or-crlf)

在不同的系统平台上使用的换行符是不一样的。UNIX/Linux 使用的是 0x0A（LF），早期的 Mac OS 使用的是 0x0D（CR），后来的 OS X 在更换内核后与 UNIX 保持一致了。但 DOS/Windows 一直使用 0x0D0A（CRLF） 作为换行符。

跨平台协作开发是常有的，不统一的换行符确实对跨平台的文件交换带来了麻烦。最大的问题是，在不同平台上，换行符发生改变时，Git 会认为整个文件被修改，这就造成我们没法 diff，不能正确反映本次的修改。还好 Git 在设计时就考虑了这一点，其提供了一个 autocrlf 的配置项，用于在提交和检出时自动转换换行符，该配置有三个可选项：

- true：提交时转换为 LF，检出时转换为 CRLF
- false：提交检出均不转换
- input：提交时转换为LF，检出时不转换

查看当前 CRLF 设置

```bash
git config core.autocrlf
```

用如下命令即可完成 CRLF 配置：

```bash
# 提交时转换为LF，检出时转换为CRLF
git config --global core.autocrlf true

# 提交时转换为LF，检出时不转换
git config --global core.autocrlf input

# 提交检出均不转换
git config --global core.autocrlf false
```

在 Windows 11 上把文件中的 CRLF 转换为 LF，可以通过 notepad++ 的 `编辑` > `文档格式转换` > `转换为 Unix (LF)` 功能转换。
