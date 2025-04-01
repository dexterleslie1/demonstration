package main

import (
	"fmt"
	"log"
	"os"
)

func main() {
	// 读取文件
	// https://tutorialedge.net/golang/reading-writing-files-in-go/
	bytes, err := os.ReadFile("./demo-import.go")
	if err != nil {
		log.Fatalf("%s", err)
	}

	fmt.Println("---------------- demo-import.go文件内容如下 ----------------")
	fmt.Print(string(bytes))

	// 写到新文件
	err = os.WriteFile("/tmp/test.go", bytes, 0664)
	if err != nil {
		log.Fatalf("%s", err)
	}
	fmt.Println("demo-import.go内容复制到文件/tmp/test.go中")

	// 追加内容到/tmp/test.go中
	file, err := os.OpenFile("/tmp/test.go", os.O_APPEND|os.O_WRONLY, 0660)
	if err != nil {
		log.Fatalf("%s", err)
	}
	defer file.Close()

	_, err = file.WriteString("// 这是追加的备注\n")
	if err != nil {
		log.Fatalf("%s", err)
	}
	fmt.Println("成功追加内容到文件/tmp/test.go中")
}
