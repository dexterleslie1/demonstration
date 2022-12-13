# golang语言基础

## go环境配置和安装

### macOS安装go

**下载go pkg并安装，下载地址：https://go.dev/dl/**

```
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

**下载go安装包，下载地址：https://go.dev/dl/**

```
# 编辑~/.bash_profile添加如下内容：
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH

# 检查
go version
```

## 编译发布golang程序

**https://freshman.tech/snippets/go/cross-compile-go-programs/**

```
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

```
go run xxx.go
```

## 使用go mod管理项目

**使用go mod 管理项目，就不需要非得把项目放到GOPATH指定目录下，你可以在你磁盘的任何位置新建一个项目**

**[go.mod go.sum](https://blog.csdn.net/Fly_as_tadpole/article/details/109441310)**

```
# 初始化一个模块名为demo-cobra，以后引用这个模块的cmd子目录方式为import "demo-cobra/cmd"，会在当前目录下生成go.mod文件
go mod init demo-cobra

# 生成go.sum文件
go mod tidy
```
***
