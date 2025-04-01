package main

import (
	"flag"
	"fmt"
)

// 演示使用flag读取命令行提供的参数
// https://blog.csdn.net/weixin_42278305/article/details/110133125
func main() {
	// 运行demo
	// go run demo-flag.go -host=192.168.1.1 -port=8081
	host := flag.String("host", "127.0.0.1" /* 不提供参数时的默认值 */, "请输入host参数")
	port := flag.Int("port", 8080, "请输入端口参数")
	flag.Parse()

	fmt.Println("host=", *host, ",port=", *port)
}
