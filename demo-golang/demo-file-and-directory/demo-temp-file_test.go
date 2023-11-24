package main

import (
	"fmt"
	"os"
	"testing"
)

func TestTempFile(t *testing.T) {
	// dir=""表示在/tmp目录下创建临时文件
	tempFile, err := os.CreateTemp("", "myfile*.log")
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	defer func(tempFile *os.File) {
		err := tempFile.Close()
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}(tempFile)

	fmt.Printf("临时文件路径 %s\n", tempFile.Name())

	err = os.WriteFile(tempFile.Name(), []byte{}, 0600)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	err = os.Remove(tempFile.Name())
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
}
