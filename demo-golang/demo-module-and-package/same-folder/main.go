package main

import "mypackage/module1"
// mypackage为go mod init mypackage时的模块名称，module2为go.mod文件下的目录名称
import "mypackage/module2"

// NOTE: 使用go mod管理依赖之后，不能使用相对路径引用
// 否则会报告 cannot find module for path xxx 错误
// import "./module1"

func main() {
	module1.Test12()
	module1.Test15()

	// 演示包名称可以和目录名称不一致
	module2x.Test()
}
