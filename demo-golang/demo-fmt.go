package main

import (
	"fmt"
	"errors"
)

// https://www.geeksforgeeks.org/fmt-errorf-function-in-golang-with-examples/
func main() {
	var err = errors.New("测试异常")
	err = fmt.Errorf("出现错误，原因：%v", err)
	fmt.Println(err)
}