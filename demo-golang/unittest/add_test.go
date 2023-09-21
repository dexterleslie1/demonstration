package main

import (
	"fmt"
	"testing"
)

// 在测试执行前先执行init函数
// https://stackoverflow.com/questions/23729790/how-can-i-do-test-setup-using-the-testing-package-in-go
func init() {
	fmt.Println("在执行测试前先加载")
}

func TestAdd(t *testing.T) {
	result := Add(1, 2)
	if result != 3 {
		t.Fatalf("预期结果:%v,实际结果:%v", 3, result)
	}

	t.Logf("测试成功")
}
