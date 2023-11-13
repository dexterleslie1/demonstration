package main

import (
	"log"
	"sync"
	"testing"
)

func TestGoroutine(t *testing.T) {
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
}
