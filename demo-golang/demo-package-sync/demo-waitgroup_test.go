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
		defer waitGroup.Done()
		log.Println("Goroutine 1")
	}()
	go func() {
		defer waitGroup.Done()
		log.Println("Groutine 2")
	}()

	// 等待两个协程结束
	waitGroup.Wait()
}
