package main

import (
	"fmt"
	"time"
)

// https://www.cnblogs.com/hi3254014978/p/16417554.html
// 在一个select语句中,go语言会按顺序从头到尾评估每一个发送和接收的语句

// 如果其中的任意一语句可以继续执行(即没有被阻塞),那么就从哪些可以执行的语句中任意选择一条来使用

// 如果没有任意一条语句可以执行(即所有的通道都被阻塞),那么有两种可能的情况:

// 如果给出了default语句,那么就会执行default语句,同时程序的执行会从select语句后的语句中恢复
// 如果没有default语句,那么select语句将被阻塞,直到至少有一个通信可以进行下去

// 防止channel超时机制
// 有时候会出现协程阻塞的情况,那么我们如何避免这个情况?我们可以使用select来设置超时
func main() {
	c := make(chan int)
	o := make(chan bool)
	go func() {
		for {
			// 下面两个case中任何一个case能够从channel中获取到数据，则select就选择获取到数据的case分支继续执行
			// 否则阻塞

			// 这种机制可以用法主线程等待协程完成某种操作后再继续执行场景，例如：在编写cli程序时候，主线程一直等待直到按下Enter按后程序才退出执行
			select {
			case v := <-c:
				fmt.Println(v)
				o <- true
			//5秒钟自动关闭,避免长时间超时
			case <-time.After(1 * time.Second):
				fmt.Println("timeout")

				// channel中放入元素true让主线程 <-o 继续执行并退出
				o <- true
				break
			}
		}
	}()
	// 相当于主线程等待协成处理完毕才退出
	<-o
	fmt.Println("程序结束")
}
