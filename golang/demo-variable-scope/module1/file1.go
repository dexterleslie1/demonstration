package module1

import "fmt"

var a1 = "这是一个全局变量"

func Test1() {
	fmt.Println("file1打印全局变量 a1=" + a1)
}
