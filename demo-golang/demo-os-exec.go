package main

import (
	"fmt"
	"log"
	"os/exec"
)

func main() {
	// exec.LookPath文档
	// https://pkg.go.dev/os/exec#LookPath
	path, err := exec.LookPath("java")
	if err != nil {
		panic(err)
	}
	fmt.Println("which java结果:", path)

	// 执行命令
	out, err := exec.Command("date").CombinedOutput()
	if err != nil {
		log.Fatal(err, " ", string(out))
	} else {
		fmt.Println("date命令执行结果:", string(out))
	}
}
