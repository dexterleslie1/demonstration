# Go



## 开发 IDE 安装和配置

> 使用vscode作为go开发ide，安装vscode之后直接创建go文件编写go代码即可从源代码执行程序，根据vscode弹出提示安装golang相关插件以便debug使用。



## 安装



### Ubuntu

参考 <a href="/dcli/README.html#安装" target="_blank">链接</a> 安装 dcli 程序

使用 dcli 程序安装 Go

```bash
sudo dcli golang install
```

安装成功后重启操作系统以加载最新的环境变量



### MacOS

通过 [链接](https://studygolang.com/dl) 下载 pkg 安装包并根据提示安装 Go

 编辑 ~/.bash_profile 添加如下内容：

```bash
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH
```

验证 Go 是否成功安装

```bash
go version
```



### CentOS8

>[参考链接](https://blog.csdn.net/shuaihj/article/details/123018041)

通过 [链接](https://studygolang.com/dl) 下载 Go Linux 发行包，例如：go1.20.12.linux-amd64.tar.gz

编辑 ~/.bash_profile 添加如下内容：

```bash
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH
```

验证 Go 是否成功安装

```bash
go version
```



### Windows Server 2016 Datacenter

> [参考链接](https://silentinstallhq.com/go-programming-language-silent-install-how-to-guide/)

通过 [链接](https://go.dev/dl/) 下载 Go MSI 安装程序并根据 MSI 提示安装 Go

设置环环境变量

```properties
GOPROXY=https://goproxy.io
GO111MODULE=on
```

打开 CMD 验证 Go 是否成功安装

```bash
go version
```



## golang语法基础

### 静态方法

> NOTE: 参考golang源代码/usr/local/software/go/src/os/exec/exec.go#func Command(name string, arg ...string) *Cmd方法总结golang静态方法的标准写法。
>
> 参考 demo-static-method

### 派生和继承

> 参考 demo-inheritance

## 编译发布golang程序

**https://freshman.tech/snippets/go/cross-compile-go-programs/**

```shell
# 发布windows 64位
GOOS=windows GOARCH=amd64 go build -o test-windows-x86_64.exe test.go

# 发布windows 32位
GOOS=windows GOARCH=386 go build -o test-windows-i386.exe test.go

# 发布macOS 64位
GOOS=darwin GOARCH=amd64 go build -o test-darwin-x86_64 test.go

# 发布macOS 32位
GOOS=darwin GOARCH=386 go build -o bin/test-darwin-i386 test.go

# 发布linux 64位
GOOS=linux GOARCH=amd64 go build -o test-linux-x86_64 test.go
```

## 使用go run从源代码运行golang程序

```shell
go run xxx.go
```



## 使用go mod管理项目

**使用go mod 管理项目，就不需要非得把项目放到GOPATH指定目录下，你可以在你磁盘的任何位置新建一个项目**

go.mod: 记录当前模块的最低go版本、模块名称、第三依赖库。require中可以指定第三方依赖库版本
go.sum: 记录第三方库被锁定的版本。
**[go.mod go.sum](https://blog.csdn.net/Fly_as_tadpole/article/details/109441310)**

```shell
# 初始化一个模块名为demo-cobra，以后引用这个模块的cmd子目录方式为import "demo-cobra/cmd"，会在当前目录下生成go.mod文件
# NOTE: 在当前目录生成go.mod文件，文件中包含module名称和最低的go版本
go mod init demo-cobra
```



### go mod tidy

```shell
# 生成go.sum文件
# NOTE: 自动推导依赖并更新到go.mod文件中，生产go.sum文件锁定当前第三方库依赖版本。
# NOTE: 会自动把项目所有依赖源代码下载到~/go/pkg/mod目录中

# NOTE: 会自动升级依赖的版本，例如: k8s.io/apiextensions-apiserver 依赖于 k8s.io/api。当前k8s.io/apiextensions-apiserver版本为v0.29.0，如果 k8s.io/api版本小于v0.29.0则会被自动升级为此版本

go mod tidy
```



## 包(package)

### 包作用

> go语言如何调用不同文件，package的作用是什么？[链接](https://www.qycn.com/xzx/article/8412.html)
>
> go语言package是golang基本的管理单元，在同一个package中可以有多个不同文件，只要每个文件的头部都有“package xxx”的相同name，就可以在主方法中使用“xxx.Method()”调用不同文件中的方法。
>
> go 依赖管理的演进经历了以下 3 个阶段：GOPATH(存在缺陷被抛弃)、go vendor(存在缺陷被抛弃)、go module(现在使用这个方法模块化) [链接](https://blog.csdn.net/Sihang_Xie/article/details/124851399)

## import、init()、main()

### 基础解析

> [链接](https://blog.csdn.net/ribavnu/article/details/51646608)
>
> main() ,init()方法是go中默认的两个方法，两个保留的关键字。
> init()方法 是在任何package中都可以出现，但是建议 每个package中只包含一个init()函数比较好，容易理解。
> 但是main() 方法只能用在package main 中。
> Go程序会自动调用init()和main()，所以你不需要在任何地方调用这两个函数。每个
> package中的init函数都是可选的，但package main就必须包含一个main函数。
> 程序的初始化和执行都起始于main包。如果main包还导入了其它的包，那么就会在编译时
> 将它们依次导入。有时一个包会被多个包同时导入，那么它只会被导入一次（例如很多包可
> 能都会用到fmt包，但它只会被导入一次，因为没有必要导入多次）。当一个包被导入时，
> 如果该包还导入了其它的包，那么会先将其它包导入进来，然后再对这些包中的包级常量
> 和变量进行初始化，接着执行init函数（如果有的话），依次类推。等所有被导入的包都加
> 载完毕了，就会开始对main包中的包级常量和变量进行初始化，然后执行main包中的
> init函数(如果存在的话)，最后执行main函数

### import用法

## 变量

### 变量赋值=和:=区别

> =是变量的单纯赋值，:=是变量的定义和赋值
>
> https://baijiahao.baidu.com/s?id=1709571399429039540&wfr=spider&for=pc

## 第三方库使用

### progressbar使用

> 参考 demo-lib-progressbar
>
> https://github.com/schollz/progressbar

### termenv使用

> 参考 demo-lib-termenv
>
> https://github.com/muesli/termenv

### reflow使用

> 参考 demo-lib-reflow
>
> https://github.com/muesli/reflow

### glamour使用

> 参考 demo-lib-glamour
>
> https://github.com/charmbracelet/glamour

### bubbletea使用

> 参考 demo-lib-bubbletea
>
> https://github.com/charmbracelet/bubbletea

### lipgloss使用

> 参考 demo-lib-lipgloss
>
> https://github.com/charmbracelet/lipgloss

### sysinfo使用

> 参考 demo-lib-sysinfo
>
> https://github.com/zcalusic/sysinfo

### emoji使用

> 参考 demo-lib-emoji
>
> https://github.com/enescakir/emoji/tree/master

### xterm使用

> 参考 demo-lib-xterm
>
> golang.org/x/term

### promptui使用

> 参考 demo-lib-promptui
>
> https://github.com/manifoldco/promptui

### properties使用

> 参考 demo-lib-properties
>
> https://github.com/magiconair/properties



## 数据类型

> 参考 demo-data-type





## go mod vendor用法

> https://blog.csdn.net/test1280/article/details/120855865



## go help 用法

### 用途

用于查看 go xxx 相关命令的帮助文档

### 用法

查看`go list`命令帮助文档

```shell
go help list
```



## go get

> https://gosamples.dev/go-get/
> The `go get` command handles package management - adding, updating, or removing dependencies in the `go.mod` file. The `go get` does not build packages.

## go install

> https://gosamples.dev/go-get/
> The [`go install`](https://go.dev/ref/mod#go-install) command builds the package and installs the executable file in the directory defined by the `GOBIN` environment variable, which defaults to the `$GOPATH/bin` path. Installing the executable file allows you to call it directly  from your system terminal by simply typing the command name, e.g. `mytool` in the command line.



## go list 用法

### 参考资料

https://dave.cheney.net/2014/09/14/go-list-your-swiss-army-knife

### 用法

返回当前目录在使用 `import` 时的路径，`-f '\{\{ .ImportPath \}\}'` 和不添加 `-f` 一致是默认格式

```shell
go list
或者
go list -f '{{ .ImportPath }}'
```

查看指定 `package import` 路径

```shell
go list github.com/juju/juju
```

查询测试文件列表

```shell
go list -f '{{ .TestGoFiles }}'
```

查询外部测试文件列表

```shell
go list -f '{{ .XTestGoFiles }}'
```

查询直接依赖列表

```shell
go list -f '{{ .Imports }}'
```

查询直接和间接依赖列表

```shell
go list -f '{{ .Deps }}'
```



## golang内置api



### packagestest相关api用法

#### 参考资料

> https://pkg.go.dev/golang.org/x/tools/go/packages/packagestest
>
> 参考 demo-package-packagestest demo





## golang marker comment(marker注释)

> https://pkg.go.dev/sigs.k8s.io/controller-tools/pkg/markers

