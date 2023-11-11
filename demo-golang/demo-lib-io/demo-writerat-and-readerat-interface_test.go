package main

import (
	"os"
	"strings"
	"testing"
)

// 测试WriterAt和ReaderAt接口
// https://books.studygolang.com/The-Golang-Standard-Library-by-Example/chapter01/01.1.html#readerat-%E5%92%8C-writerat-%E6%8E%A5%E5%8F%A3
func TestWriterAtAndReaderAtInterface(t *testing.T) {
	// 测试ReaderAt
	reader := strings.NewReader("Go语言中文网")
	p := make([]byte, 6)
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

	// 测试WriterAt
	filename := "/tmp/writeat.txt"
	file, err := os.Create(filename)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	defer file.Close()
	n, err = file.WriteString("Golang中文社区——这里是多余")
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strData := []byte("Go语言中文网")
	n, err = file.WriteAt(strData, 24)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len(strData) != n {
		t.Errorf("expected %d, got %d", len(strData), n)
	}
}
