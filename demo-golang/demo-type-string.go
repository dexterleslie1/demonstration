package main

import "fmt"

func main() {
	// 测试string数据类型

	// 判断string长度
	myString := ""
	if len(myString) == 0 {
		fmt.Println("myString字符串为空")
	} else {
		fmt.Println("myString字符串不为空")
	}

	// 判断字符串是否空
	myString1 := " "
	if len(myString1) == 0 {
		fmt.Println("myString1字符串为空")
	} else {
		fmt.Println("myString1字符串不为空")
	}
}
