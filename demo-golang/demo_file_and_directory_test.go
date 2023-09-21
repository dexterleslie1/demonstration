package main

import (
	"bytes"
	"crypto/rand"
	"errors"
	"fmt"
	"os"
	"os/exec"
	"path"
	"testing"
)

func TestFileAndDirectory(t *testing.T) {
	// 用户home目录获取
	// https://stackoverflow.com/questions/7922270/obtain-users-home-directory
	homeDirectory, err := os.UserHomeDir()
	if err != nil {
		panic("无法获取用户home目录：" + err.Error())
	}

	// https://stackoverflow.com/questions/46748636/how-to-create-new-file-using-go-script
	// 写文件，如果文件不存在创建并写入，如果文件存在则直接覆盖写入
	err = os.WriteFile(homeDirectory+"/.1.txt", []byte("Hello\n"), 0755)
	if err != nil {
		fmt.Printf("Unable to write file: %v", err)
	}

	// https://stackoverflow.com/questions/12518876/how-to-check-if-a-file-exists-in-go
	// 判断文件已经存在
	if _, err := os.Stat(homeDirectory + "/.1.txt"); errors.Is(err, os.ErrNotExist) {
		panic("预期文件" + homeDirectory + "/.1.txt不存在")
	}

	// https://www.geeksforgeeks.org/how-to-delete-or-remove-a-file-in-golang/
	// 删除文件
	e := os.Remove(homeDirectory + "/.1.txt")
	if e != nil {
		panic("删除文件" + homeDirectory + "/.1.txt失败")
	}

	// 判断文件不存在
	if _, err := os.Stat(homeDirectory + "/.1.txt"); err == nil {
		panic("预期文件" + homeDirectory + "/.1.txt存在")
	}

	/* 测试path.Join */
	fullPath := path.Join("1", "2", "3")
	fmt.Println("完整路径:", fullPath)

	/* 测试mv命令 */
	// https://blog.csdn.net/whatday/article/details/109315495
	oldLocation := homeDirectory + "/.test.txt"
	newLocation := homeDirectory + "/.newtest.txt"
	var mvCmd *exec.Cmd = exec.Command("mv", oldLocation, newLocation)
	// CombinedOutput表示标准输出和错误输出合成
	// https://stackoverflow.com/questions/1877045/how-do-you-get-the-output-of-a-system-command-in-go
	out, err := mvCmd.CombinedOutput()
	if err != nil {
		fmt.Println("mv命令失败", err, string(out))
	} else {
		fmt.Println("成功移动./.test.txt到./.newtest.txt")
	}

	// 判断文件权限
	err = os.WriteFile(homeDirectory+"/.1.txt", []byte(""), 0755)
	if err != nil {
		fmt.Printf("Unable to write file: %v", err)
	}
	info, err := os.Stat(homeDirectory + "/.1.txt")
	if err != nil {
		panic(err)
	}
	rwxString := ""
	// 判断owner权限
	if info.Mode()&(1<<uint(8)) != 0 {
		rwxString = rwxString + "r"
	} else {
		rwxString = rwxString + "-"
	}
	if info.Mode()&(1<<uint(7)) != 0 {
		rwxString = rwxString + "w"
	} else {
		rwxString = rwxString + "-"
	}
	if info.Mode()&(1<<uint(6)) != 0 {
		rwxString = rwxString + "x"
	} else {
		rwxString = rwxString + "-"
	}
	// 判断group权限
	if info.Mode()&(1<<uint(5)) != 0 {
		rwxString = rwxString + "r"
	} else {
		rwxString = rwxString + "-"
	}
	if info.Mode()&(1<<uint(4)) != 0 {
		rwxString = rwxString + "w"
	} else {
		rwxString = rwxString + "-"
	}
	if info.Mode()&(1<<uint(3)) != 0 {
		rwxString = rwxString + "x"
	} else {
		rwxString = rwxString + "-"
	}
	// 判断other权限
	if info.Mode()&(1<<uint(2)) != 0 {
		rwxString = rwxString + "r"
	} else {
		rwxString = rwxString + "-"
	}
	if info.Mode()&(1<<uint(1)) != 0 {
		rwxString = rwxString + "w"
	} else {
		rwxString = rwxString + "-"
	}
	if info.Mode()&(1<<uint(0)) != 0 {
		rwxString = rwxString + "x"
	} else {
		rwxString = rwxString + "-"
	}
	fmt.Println("文件"+homeDirectory+"/.1.txt"+"权限", rwxString)

	//#region 在临时目录创建新的文件

	randomBytes := make([]byte, 1024*1024)
	_, err = rand.Read(randomBytes)
	if err != nil {
		t.Fatalf("rand.Read调用失败，原因: %s", err)
	}

	tempDirectory := os.TempDir()
	tempFile := tempDirectory + "/.1.bin"
	err = os.WriteFile(tempFile, randomBytes, 0755)
	if err != nil {
		t.Fatalf("%s", err)
	}

	tempBytes, err := os.ReadFile(tempFile)
	if !bytes.Equal(randomBytes, tempBytes) {
		t.Fatalf("写入文件的byte array和读取文件的byte array不匹配")
	}

	//#endregion
}
