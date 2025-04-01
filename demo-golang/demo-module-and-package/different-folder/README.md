# 调用不同项目的模块

## 运行

```shell
# 启用GO111MODULE=on，此环境变量是启用go mod模块管理特性
MacOSdeMBP:mypackagedebuging macos$ export GO111MODULE=on
MacOSdeMBP:mypackagedebuging macos$ echo $GO111MODULE
on

# 执行main.go
MacOSdeMBP:mypackagedebuging macos$ go run main.go 
module1 Test method!
module2 Test method!
```

