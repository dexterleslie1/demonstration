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
		log.Println("Goroutine 1")
		waitGroup.Done()
	}()
	go func() {
		log.Println("Groutine 2")
		waitGroup.Done()
	}()

	// 等待两个协程结束
	waitGroup.Wait()

	//#endregion

	//#region 协程没有父子关系，他们是同级的

	go f1()
	time.Sleep(2 * time.Second)
	log.Println("main finished")

	//#endregion
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
