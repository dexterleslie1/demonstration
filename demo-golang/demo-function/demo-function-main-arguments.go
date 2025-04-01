package main

import (
	"fmt"
	"os"
)

// 解析command line arguments
// https://stackoverflow.com/questions/2707434/how-to-access-command-line-arguments-passed-to-a-go-program

// 运行程序 go run main-function-arguments.go 1 2 3
func main() {
	argsWithProg := os.Args
	argsWithoutProg := os.Args[1:]
	arg := os.Args[3]
	fmt.Println(argsWithProg)
	fmt.Println(argsWithoutProg)
	fmt.Println(arg)
}
