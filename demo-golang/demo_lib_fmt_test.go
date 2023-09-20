package main

import (
	"errors"
	"fmt"
	"testing"
)

func TestErrorf(t *testing.T) {
	var err = errors.New("测试异常")
	err = fmt.Errorf("出现错误，原因：%v", err)
	if "*errors.errorString" != fmt.Sprintf("%T", err) {
		t.Fatalf("没有预期值")
	}
	if "出现错误，原因：测试异常" != err.Error() {
		t.Fatalf("没有预期值")
	}

	// 十六进制表示，字母形式为小写 a-f
	// https://www.geeksforgeeks.org/fmt-errorf-function-in-golang-with-examples/
	// https://blog.csdn.net/zhangkaiadl/article/details/107931333
	str := fmt.Sprintf("数值13十六进制:%x", 13)
	if "数值13十六进制:d" != str {
		t.Fatalf("没有预期值")
	}
}

// https://pkg.go.dev/fmt
func TestFormat(t *testing.T) {
	// 获取变量类型
	var myVar int = 23
	typeString := fmt.Sprintf("%T", myVar)
	if "int" != typeString {
		t.Fatalf("没有预期值")
	}

	// 打印整形变量
	myVar = 25
	myVarStr := fmt.Sprintf("%d", myVar)
	if "25" != myVarStr {
		t.Fatalf("没有预期值")
	}

	// 打印浮点型变量
	var myVarFloat float64 = 3.14
	myVarStr = fmt.Sprintf("%f", myVarFloat)
	if "3.140000" != myVarStr {
		t.Fatalf("没有预期值")
	}
}
