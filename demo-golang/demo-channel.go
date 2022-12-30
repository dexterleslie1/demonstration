package main

import "fmt"

func Test1() {
	// 定义channel
	intChannel := make(chan int, 3)
	intChannel <- 1
	intChannel <- 2
	intChannel <- 3
	// 不能超过channel的容量3，否则会报告 fatal error: all goroutines are asleep - deadlock!
	// intChannel <- 5

	// 丢弃一个元素
	<-intChannel
	intChannel <- 5

	<-intChannel
	<-intChannel
	<-intChannel
	// channel里没有数据后不能获取，否则报告fatal error: all goroutines are asleep - deadlock!
	// <-intChannel
}

// 测试Close
func TestClose() {
	intChannel := make(chan int, 3)
	intChannel <- 1
	intChannel <- 2
	close(intChannel)
	// 不能向关闭的channel添加元素，否则报告 panic: send on closed channel
	// intChannel <- 3

	// 能够读取关闭channel中的元素
	<-intChannel
}

// 测试使用range遍历
func TestIterate() {
	intChannel := make(chan int, 3)
	intChannel <- 1
	intChannel <- 2

	// 遍历前需要关闭channel，否则 fatal error: all goroutines are asleep - deadlock!
	close(intChannel)
	fmt.Println("intChannel内容如下:")
	for value := range intChannel {
		fmt.Println("\t", value)
	}
}

// 演示channel使用
func main() {
	// Test1()
	// TestClose()
	TestIterate()
}
