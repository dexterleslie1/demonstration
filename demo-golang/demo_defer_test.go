// 演示defer用法
// https://go.dev/tour/flowcontrol/12

package main

import (
	"fmt"
	"testing"
)

func TestDefer(t *testing.T) {
	// 函数返回时才执行defer语句
	defer fmt.Println("world")

	fmt.Println("hello")
}
