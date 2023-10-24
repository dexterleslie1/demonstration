package main

import (
	"bufio"
	"fmt"
	"io"
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

	// 显示进度的exec
	// https://stackoverflow.com/questions/65625672/get-output-of-os-exec-to-to-show-progress-executed-command
	cmd := exec.Command("./demo-os-exec-scripts.sh")

	stdout, err := cmd.StdoutPipe()
	if err != nil {
		log.Fatal("获取stdout pipe错误，原因: ", err)
	}
	stderr, err := cmd.StderrPipe()
	if err != nil {
		log.Fatal("获取stderr pipe错误，原因: ", err)
	}
	err = cmd.Start()
	if err != nil {
		log.Fatal("调用cmd.Start()错误，原因: ", err)
	}

	scanner := bufio.NewScanner(io.MultiReader(stdout, stderr))
	// scanner.Split(bufio.ScanWords)
	for scanner.Scan() {
		m := scanner.Text()
		fmt.Println(m)
	}
	err = cmd.Wait()
	if err != nil {
		log.Fatal("调用cmd.Wait()错误，原因: ", err)
	}
}
