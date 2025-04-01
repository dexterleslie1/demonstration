package main

import (
	"bytes"
	"testing"
)

// 演示bytes.Buffer用法
// https://cloud.tencent.com/developer/article/1456243
func TestBuffer(t *testing.T) {
	//#region 构造bytes.Buffer方法

	// 使用new方法创建bytes.Buffer
	str := "Go语言中文网"
	buffer := new(bytes.Buffer)
	n, err := buffer.WriteString(str)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len([]byte(str)) != n {
		t.Fatalf("expected %d, got %d", len([]byte(str)), n)
	}
	n, err = buffer.Read([]byte(str))
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len([]byte(str)) != n {
		t.Fatalf("expected %d, got %d", len([]byte(str)), n)
	}

	// 使用bytes.NewBuffer方法创建bytes.Buffer
	str = "Go语言中文网"
	buffer = bytes.NewBuffer([]byte(str))
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len([]byte(str)) != n {
		t.Fatalf("expected %d, got %d", len([]byte(str)), n)
	}
	n, err = buffer.Read([]byte(str))
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len([]byte(str)) != n {
		t.Fatalf("expected %d, got %d", len([]byte(str)), n)
	}

	// 使用bytes.NewBufferString方法创建bytes.Buffer
	str = "Go语言中文网"
	buffer = bytes.NewBufferString(str)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len([]byte(str)) != n {
		t.Fatalf("expected %d, got %d", len([]byte(str)), n)
	}
	n, err = buffer.Read([]byte(str))
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if len([]byte(str)) != n {
		t.Fatalf("expected %d, got %d", len([]byte(str)), n)
	}

	//#endregion

	//#region todo bytes.Buffer写数据方法

	//#endregion

	//#region todo bytes.Buffer读数据方法

	//#endregion

}
