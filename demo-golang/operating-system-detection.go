package main

import (
	"fmt"
	"runtime"
)

func main() {
	// macOS为darwin
	// windows为windows
	// ubuntu、centOS为linux
	fmt.Println("runtime.GOOS=" + runtime.GOOS)
}