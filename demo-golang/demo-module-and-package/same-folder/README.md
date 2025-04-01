# 调用同一个项目下的包

## 运行demo

```shell
# 启用GO111MODULE=on，此环境变量是启用go mod模块管理特性
MacOSdeMBP:mypackagedebuging macos$ export GO111MODULE=on
MacOSdeMBP:mypackagedebuging macos$ echo $GO111MODULE
on

# 初始化go.mod文件，mypackage为import时使用的包名称
MacOSdeMBP:same-folder macos$ go mod init mypackage
go: creating new go.mod: module mypackage
go: to add module requirements and sums:
        go mod tidy

# 运行main.go
MacOSdeMBP:same-folder macos$ go run main.go  
这是module1.go的init方法
这是module12.go的init方法
这是module1的Test12方法
这是module1的方法Test15
这是module2的Test方法
```



