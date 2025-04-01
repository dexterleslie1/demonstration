package main

import (
	"log"
	"runtime/debug"
)

func main() {
	// https://stackoverflow.com/questions/19094099/how-to-dump-goroutine-stacktraces
	// https://stackoverflow.com/questions/68339988/how-to-disable-stack-trace-info-in-go-for-any-os-executing-the-binary
	defer func() {
		if r := recover(); r != nil {
			debug.PrintStack()
			log.Fatalf("意料之外错误，原因：%s", r)
		}
	}()

	method1()
}
