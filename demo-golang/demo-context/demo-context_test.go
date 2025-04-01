package main

import (
	"context"
	"fmt"
	"strings"
	"testing"
	"time"
)

// Context is a built-in package in the Go standard library that provides a
// powerful toolset for managing concurrent operations.
// It enables the propagation of cancellation signals, deadlines, and values across goroutines,
//ensuring that related operations can gracefully terminate when necessary.
//With context, you can create a hierarchy of goroutines and pass important information down the chain.

// https://www.digitalocean.com/community/tutorials/how-to-use-contexts-in-go
// https://medium.com/@jamal.kaksouri/the-complete-guide-to-context-in-golang-efficient-concurrency-management-43d722f6eaea
func TestContextWithValue(t *testing.T) {
	ctx := context.Background()
	ctx = context.WithValue(ctx, "myKey", "myValue")
	doSomething(ctx)

}

func doSomething(ctx context.Context) {
	fmt.Printf("doSomething: myKey's value is %s\n", ctx.Value("myKey"))

	ctx = context.WithValue(ctx, "myKey", "myValueAnother")
	ctx = context.WithValue(ctx, "myKey1", "myValue1")
	doAnother(ctx)
}

func doAnother(ctx context.Context) {
	fmt.Printf("doAnother: myKey's value is %s\n", ctx.Value("myKey"))
	fmt.Printf("doAnother: myKey1's value is %s\n", ctx.Value("myKey1"))
}

func TestContextWithCancel(t *testing.T) {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)

	printCh := make(chan int)
	go doAnotherWithCancel(ctx, printCh)

	for num := 1; num <= 3; num++ {
		printCh <- num
	}

	// 调用cancel方法让goroutine退出
	cancel()

	time.Sleep(100 * time.Millisecond)

	fmt.Printf("TestContextWithCancel: finished\n")
}

func doAnotherWithCancel(ctx context.Context, printCh <-chan int) {
	defer func() {
		fmt.Printf("doAnotherWithCancel: finished\n")
	}()

	for {
		select {
		case <-ctx.Done():
			if err := ctx.Err(); err != nil {
				fmt.Printf("doAnotherWithCancel err: %s\n", err)
			}
			return
		case num := <-printCh:
			fmt.Printf("doAnotherWithCancel: %d\n", num)
		}
	}
}

func TestContextWithDeadline(t *testing.T) {
	ctx := context.Background()
	ctx, cancel := context.WithDeadline(ctx, time.Now().Add(1500*time.Millisecond))
	// 无论什么原因保证最后释放goroutine
	defer cancel()

	printCh := make(chan int)
	go doAnotherWithDeadline(ctx, printCh)

	for num := 1; num <= 3; num++ {
		if safeSend(printCh, num) {
			break
		}
		time.Sleep(time.Second)
	}

	time.Sleep(100 * time.Millisecond)
	fmt.Println("TestContextWithDeadline: finished")
}

func safeSend(printCh chan int, value int) (chanClosed bool) {
	defer func() {
		if err := recover(); err != nil {
			// 表示channel已经关闭
			if strings.Contains(err.(error).Error(), "closed channel") {
				chanClosed = true
			} else {
				chanClosed = false
			}
		}
	}()

	// 如果channel关闭会触发recover函数
	printCh <- value
	return false
}

func safeClose(printCh chan int) (justClosed bool) {
	defer func() {
		if err := recover(); err != nil {
			if strings.Contains(err.(error).Error(), "close of closed channel") {
				justClosed = false
			} else {
				justClosed = true
			}
		}
	}()

	close(printCh)
	return true
}

func doAnotherWithDeadline(ctx context.Context, printCh chan int) {
	defer func() {
		fmt.Println("doAnotherWithDeadline: finished")
	}()

	for {
		select {
		case <-ctx.Done():
			if err := ctx.Err(); err != nil {
				fmt.Printf("doAnotherWithDeadline err: %s\n", err)
			}
			safeClose(printCh)
			return
		case num := <-printCh:
			fmt.Printf("doAnotherWithDeadline: %d\n", num)
		}
	}
}

func TestContextWithTimeout(t *testing.T) {
	ctx := context.Background()
	ctx, cancel := context.WithTimeout(ctx, 1500*time.Millisecond)
	defer cancel()

	printCh := make(chan int)
	go doAnotherWithTimeout(ctx, printCh)

	for i := 1; i <= 100; i++ {
		if safeSend(printCh, i) {
			break
		}
		time.Sleep(time.Second)
	}

	time.Sleep(100 * time.Millisecond)
	fmt.Println("TestContextWithTimeout: finished")
}

func doAnotherWithTimeout(ctx context.Context, printCh chan int) {
	defer func() {
		fmt.Println("doAnotherWithTimeout: finished")
	}()

	for {
		select {
		case <-ctx.Done():
			if err := ctx.Err(); err != nil {
				fmt.Printf("doAnotherWithTimeout err: %s\n", err)
			}
			safeClose(printCh)
			return
		case num := <-printCh:
			fmt.Printf("doAnotherWithTimeout: %d\n", num)
		}
	}
}
