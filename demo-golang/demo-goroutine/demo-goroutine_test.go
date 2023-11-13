package demo_goroutine

import (
	"log"
	"sync"
	"testing"
	"time"
)

func TestGoroutine(t *testing.T) {
	//#region 启动两个协程
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(2)

	go func() {
		defer waitGroup.Done()
		log.Println("Goroutine 1")
	}()
	go func() {
		defer waitGroup.Done()
		log.Println("Groutine 2")
	}()

	// 等待两个协程结束
	waitGroup.Wait()

	//#endregion

	//#region 协程没有父子关系，他们是同级的

	go f1()
	time.Sleep(2 * time.Second)
	log.Println("main after f2 finished")

	//#endregion

	//region 演示recover机制

	go f3()
	time.Sleep(time.Second)
	log.Println("main after f3 finished")

	//endregion
}

func f1() {
	// 在f1函数中启动一个新协程运行f2函数后，f1函数马上结束，
	// f2仍然能够执行完成，说明f1函数执行完毕并不影响新协程的执行
	go f2()
	log.Println("f1 finished")
}

func f2() {
	time.Sleep(time.Second)
	log.Println("f2 finished")
}

func f3() {
	// 通过此recover机制就不会导致整个程序崩溃
	defer func() {
		err := recover()
		if err != nil {
			log.Printf("f3发生错误 %s\n", err)
		}
	}()
	a, b := 1, 0
	// 此处会发生panic错误导致整个程序崩溃
	_ = a / b
	log.Println("f3 finished")
}
