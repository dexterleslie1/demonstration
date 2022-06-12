# golang语言基础

## macOS安装go

**下载go pkg并安装**

```
# 编辑~/.bash_profile添加如下内容：
export GOPROXY=https://goproxy.io
export GO111MODULE=on
PATH=$PATH:~/go/bin
export PATH

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
