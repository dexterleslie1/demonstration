package main

import (
	"bufio"
	"bytes"
	"os"
	"strings"
	"testing"
)

func TestWriterToAndReaderFromInterface(t *testing.T) {
	// 测试ReaderFrom
	str := "Go语言中文网\n"
	reader := strings.NewReader(str)
	writer := bufio.NewWriter(os.Stdout)
	n, err := writer.ReadFrom(reader)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	writer.Flush()
	if len([]byte(str)) != int(n) {
		t.Errorf("expected %d, got %d", len([]byte(str)), n)
	}

	// 测试WriterTo
	strData := []byte("Go语言中文网\n")
	bytesReader := bytes.NewReader(strData)
	n, err = bytesReader.WriteTo(os.Stdout)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len(strData) != int(n) {
		t.Errorf("expected %d, got %d", len(strData), n)
	}
}

// todo 未完成下面学习
// https://books.studygolang.com/The-Golang-Standard-Library-by-Example/chapter01/01.0.html
