package main

import (
	"log"
	"sync"
	"testing"
)

var count int32
var lock = sync.Mutex{}

func f1() {
	// 没有使用并发控制时，count最终不是预期2000000
	//for i := 0; i < 1000000; i++ {
	//	count++
	//}

	for i := 0; i < 1000000; i++ {
		lock.Lock()
		count++
		lock.Unlock()
	}
}

func TestMutex(t *testing.T) {
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(2)

	go func() {
		defer waitGroup.Done()
		f1()
		log.Printf("f1 count=%d\n", count)
	}()

	go func() {
		defer waitGroup.Done()
		f1()
		log.Printf("f1 count=%d\n", count)
	}()

	waitGroup.Wait()

	log.Printf("main count=%d\n", count)
}
