package main

import (
	"fmt"
	"log"
	"strings"
	"sync"
	"testing"
	"time"
)

//region 测试容量为0的管道

func TestChannelWithCapacityZero(t *testing.T) {
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(2)

	myChan := make(chan string, 0)

	log.Println("开始管道测试，此日志用于记录开始时间")

	go func() {
		defer waitGroup.Done()
		// 无法往容量为0的管道放入数据，此时阻塞
		myChan <- ""
		log.Println("往管道放入数据")
	}()

	time.Sleep(2 * time.Second)

	go func() {
		defer waitGroup.Done()
		// 读取管道数据请求到达后，上面往管道放入数据阻塞才能够终止继续执行代码
		<-myChan
		log.Println("读取管道数据")
	}()

	waitGroup.Wait()
}

//endregion

//region 测试容量为1的管道

// 先放后读
func TestChannelWithCapacityOneSituation1(t *testing.T) {
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(2)

	myChan := make(chan string, 1)

	log.Println("开始管道测试，此日志用于记录开始时间")

	go func() {
		defer waitGroup.Done()
		// 因为管道容量为1，所以能够马上放入数据
		myChan <- ""
		log.Println("往管道放入数据")
	}()

	time.Sleep(2 * time.Second)

	go func() {
		defer waitGroup.Done()
		<-myChan
		log.Println("读取管道数据")
	}()

	waitGroup.Wait()
}

// 先读后放
func TestChannelWithCapacityOneSituation2(t *testing.T) {
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(2)

	myChan := make(chan string, 1)

	log.Println("开始管道测试，此日志用于记录开始时间")

	go func() {
		defer waitGroup.Done()
		<-myChan
		log.Println("读取管道数据")
	}()

	time.Sleep(2 * time.Second)

	go func() {
		defer waitGroup.Done()
		// 因为管道容量为1，所以能够马上放入数据
		myChan <- ""
		log.Println("往管道放入数据")
	}()

	waitGroup.Wait()
}

//endregion

//region 测试生产和消费

func TestProduceAndConsume(t *testing.T) {
	myChan := make(chan int, 100)

	// 生产者等待组
	awaitGroupProducer := sync.WaitGroup{}
	awaitGroupProducer.Add(2)

	// 创建两个生产者协程
	go func() {
		defer awaitGroupProducer.Done()
		for i := 0; i < 10; i++ {
			myChan <- 5
		}
	}()
	go func() {
		defer awaitGroupProducer.Done()
		for i := 0; i < 10; i++ {
			myChan <- 5
		}
	}()

	// 消费者等待组
	awaitGroupConsumer := sync.WaitGroup{}
	awaitGroupConsumer.Add(1)
	go func() {
		defer awaitGroupConsumer.Done()
		sum := 0
		for {
			n, ok := <-myChan

			// 管道关闭后并且里面所有数据被读取，则ok=false
			if !ok {
				break
			}

			sum += n
		}

		log.Printf("sum=%d\n", sum)
	}()

	// 等待生产者执行完毕
	awaitGroupProducer.Wait()

	// 关闭管道以便消费者协程退出
	// 管道关闭后不能够再写入数据，但依旧能够读取里面未读取的数据
	close(myChan)

	// 等待消费者执行完毕
	awaitGroupConsumer.Wait()
}

//endregion

//region 测试主协程使用管道等待协程结束

func TestMainRoutineAwaitByUsingChannel(t *testing.T) {
	myChan := make(chan int, 100)

	// 生产者等待组
	awaitGroupProducer := sync.WaitGroup{}
	awaitGroupProducer.Add(2)

	// 创建两个生产者协程
	go func() {
		defer awaitGroupProducer.Done()
		for i := 0; i < 10; i++ {
			myChan <- 5
		}
	}()
	go func() {
		defer awaitGroupProducer.Done()
		for i := 0; i < 10; i++ {
			myChan <- 5
		}
	}()

	// 消费者等待管道
	awaitChannelConsumer := make(chan struct{}, 0)

	go func() {
		sum := 0
		for {
			n, ok := <-myChan

			// 管道关闭后并且里面所有数据被读取，则ok=false
			if !ok {
				break
			}

			sum += n
		}

		log.Printf("sum=%d\n", sum)

		// 往管道中放入数据，以便主协程退出
		awaitChannelConsumer <- struct{}{}
	}()

	// 等待生产者执行完毕
	awaitGroupProducer.Wait()

	// 关闭管道以便消费者协程退出
	// 管道关闭后不能够再写入数据，但依旧能够读取里面未读取的数据
	close(myChan)

	// 等待消费者执行完毕
	<-awaitChannelConsumer
}

//endregion

//region 函数返回channel

// https://stackoverflow.com/questions/28602308/returning-channels-golang
func TestFuncReturnChannel(t *testing.T) {
	out := gen([]int{1, 2, 3, 4, 5, 6})
	ok := true
	for ok {
		// channel被关闭后，okT=false
		n, okT := <-out
		ok = okT
		if ok {
			fmt.Printf("get ok, n=%d\n", n)
		} else {
			fmt.Printf("get not ok, n=%d\n", n)
		}
	}
}

func gen(numbers []int) <-chan int {
	out := make(chan int)
	go func() {
		for _, n := range numbers {
			out <- n
			time.Sleep(time.Second)
		}

		close(out)
	}()

	fmt.Println("return statement is called")
	return out
}

//endregion

//region 重复关闭channel panic处理

// https://go101.org/article/channel-closing.html
func TestRecoverPanicFromCloseOfClosedChannel(t *testing.T) {
	ch := make(chan int)
	safeClose(ch)
	safeClose(ch)
}

func safeClose(ch chan int) (justClosed bool) {
	defer func() {
		if err := recover(); err != nil {
			if strings.Contains(err.(error).Error(), "close of closed channel") {
				justClosed = false
			} else {
				justClosed = true
			}
		}
	}()

	close(ch)
	return true
}

//endregion

//region 发送数据到已经关闭channel panic处理

// https://go101.org/article/channel-closing.html
func TestRecoverPanicFromSendDataToClosedChannel(t *testing.T) {
	ch := make(chan int)

	go closeChannelAfter1500Milliseconds(ch)

	for i := 0; i < 100; i++ {
		if !safeSend(ch, i) {
			break
		}
		time.Sleep(time.Second)
	}
	fmt.Println("TestRecoverPanicFromSendDataToClosedChannel: finished")
}

func safeSend(ch chan int, value int) (sent bool) {
	defer func() {
		if err := recover(); err != nil {
			if strings.Contains(err.(error).Error(), "send on closed channel") {
				sent = false
			} else {
				sent = true
			}
		}
	}()

	ch <- value
	return true
}

func closeChannelAfter1500Milliseconds(ch chan int) {
	time.Sleep(1500 * time.Millisecond)
	close(ch)
}

//endregion
