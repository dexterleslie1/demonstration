package main

import (
	"log"
	"sync"
	"sync/atomic"
	"testing"
)

var atomicCount int32

func atomicF1() {
	// 没有使用并发控制时，count最终不是预期2000000
	//for i := 0; i < 1000000; i++ {
	//	count++
	//}

	for i := 0; i < 1000000; i++ {
		atomic.AddInt32(&atomicCount, 1)
	}
}

func TestAtomic(t *testing.T) {
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(2)

	go func() {
		defer waitGroup.Done()
		atomicF1()
		log.Printf("f1 count=%d\n", atomicCount)
	}()

	go func() {
		defer waitGroup.Done()
		atomicF1()
		log.Printf("f1 count=%d\n", atomicCount)
	}()

	waitGroup.Wait()

	log.Printf("main count=%d\n", atomicCount)
}
