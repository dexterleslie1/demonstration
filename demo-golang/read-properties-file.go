package main

import (
	"fmt"
	"github.com/magiconair/properties"
)

// https://pkg.go.dev/github.com/magiconair/properties#section-readme
// 使用go mod配置github.com/magiconair/properties第三方依赖
// 在当前目录下生成go.mod文件：go mod init demo-golang
// 配置第三方依赖使其可以通过import使用：go mod tidy
func main() {
	varProperties := properties.MustLoadFile("./config.properties", properties.UTF8)
	fmt.Println(varProperties)
}
