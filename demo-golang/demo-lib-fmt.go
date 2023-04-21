package main

import (
	"errors"
	"fmt"
)

func TestError() {
	var err = errors.New("测试异常")
	err = fmt.Errorf("出现错误，原因：%v", err)
	fmt.Println(err)
}

// https://www.geeksforgeeks.org/fmt-errorf-function-in-golang-with-examples/
func main() {
	// TestError()

	// Sprintf
	// 十六进制表示，字母形式为小写 a-f
	// https://blog.csdn.net/zhangkaiadl/article/details/107931333
	fmt.Printf("数值13十六进制:%x\n", 13)
}
