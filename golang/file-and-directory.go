package main

import (
	"os"
	"fmt"
	"errors"
)

func main() {
	// https://stackoverflow.com/questions/46748636/how-to-create-new-file-using-go-script
	// 写文件，如果文件不存在创建并写入，如果文件存在则直接覆盖写入
	err := os.WriteFile("/tmp/1.txt", []byte("Hello\n"), 0755)
    if err != nil {
        fmt.Printf("Unable to write file: %v", err)
    }

	// https://stackoverflow.com/questions/12518876/how-to-check-if-a-file-exists-in-go
	// 判断文件已经存在
	if _, err := os.Stat("/tmp/1.txt"); errors.Is(err, os.ErrNotExist) {
		panic("预期文件/tmp/1.txt不存在")
	}

	// https://www.geeksforgeeks.org/how-to-delete-or-remove-a-file-in-golang/
	// 删除文件
	e := os.Remove("/tmp/1.txt")
    if e != nil {
        panic("删除文件/tmp/1.txt失败")
    }

	// 判断文件不存在
	if _, err := os.Stat("/tmp/1.txt"); err == nil {
		panic("预期文件/tmp/1.txt存在")
	}
}