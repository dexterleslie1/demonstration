# golang语言基础

## 开发ide安装和配置

> 使用vscode作为go开发ide，安装vscode之后直接创建go文件编写go代码即可从源代码执行程序，根据vscode弹出提示安装golang相关插件以便debug使用。

## go环境配置和安装

> 使用dcli程序安装go环境
> 安装命令 dcli go install

### macOS安装go

**下载go pkg并安装，下载地址：https://studygolang.com/dl**

```shell
# 编辑~/.bash_profile添加如下内容：
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH

# 检查
go version
```

### centOS8安装go

**参考资料：https://blog.csdn.net/shuaihj/article/details/123018041**

**下载go安装包，下载地址：https://studygolang.com/dl**

```shell
# 编辑~/.bash_profile添加如下内容：
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH

# 检查
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

go.mod: 记录当前模块的最低go版本、模块名称、第三依赖库。
go.sum: 记录第三方库被锁定的版本。
**[go.mod go.sum](https://blog.csdn.net/Fly_as_tadpole/article/details/109441310)**

```shell
# 初始化一个模块名为demo-cobra，以后引用这个模块的cmd子目录方式为import "demo-cobra/cmd"，会在当前目录下生成go.mod文件
# NOTE: 在当前目录生成go.mod文件，文件中包含module名称和最低的go版本
go mod init demo-cobra

# 生成go.sum文件
# NOTE: 自动推倒依赖并更新到go.mod文件中，生产go.sum文件锁定当前第三方库依赖版本。
# NOTE: 会自动把项目所有依赖源代码下载到~/go/pkg/mod目录中
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
