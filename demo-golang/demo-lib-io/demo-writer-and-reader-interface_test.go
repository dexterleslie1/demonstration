package main

import (
	"os"
	"strings"
	"testing"
)

func TestStringReader(t *testing.T) {
	// 使用字符串创建Reader
	reader := strings.NewReader("Go语言中文网")
	p := make([]byte, 6)
	// 在reader的字节偏移2处开始读取6个字节数据
	n, err := reader.ReadAt(p, 2)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if 6 != n {
		t.Errorf("expected %d, got %d", 6, n)
	}
	if "语言" != string(p) {
		t.Errorf("expected %s, got %s", "语言", string(p))
	}
}

func TestFileReaderAndWriter(t *testing.T) {
	filename := "/tmp/writeAt.txt"

	// File实现了Reader和Writer接口
	file, err := os.Create(filename)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	defer file.Close()

	strBytes := []byte("Golang中文社区——这里是多余")
	n, err := file.Write(strBytes)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len(strBytes) != n {
		t.Errorf("expected %d, got %d", len(strBytes), n)
	}

	n, err = file.WriteAt([]byte("Go语言中文网"), 24)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if 17 != n {
		t.Errorf("expected %d, got %d", 17, n)
	}

	// 文件指针
	file.Seek(0, 0)
	fileData := make([]byte, 1024)
	n, err = file.Read(fileData)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	fileData = fileData[:n]
	if "Golang中文社区——Go语言中文网" != string(fileData) {
		t.Errorf("expected %s, got %s", "Golang中文社区——Go语言中文网", string(fileData))
	}
}
