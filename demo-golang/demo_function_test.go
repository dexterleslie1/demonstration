// 演示golang函数
package main

import "testing"

// 测试函数参数为指针
// https://www.geeksforgeeks.org/function-arguments-in-golang/
func Swap(a, b *int) {
	var tmp int

	tmp = *a
	*a = *b
	*b = tmp
}
func TestSwap(t *testing.T) {
	a := 10
	b := 20
	Swap(&a, &b)

	if a != 20 {
		t.Errorf("a must be 20")
	}
	if b != 10 {
		t.Errorf("b must be 10")
	}
}

// 测试函数返回多个值
func FuncReturnMultipleValues() (int, int) {
	return 2, 3
}
func TestFuncReturnMultipleValues(t *testing.T) {
	a, b := FuncReturnMultipleValues()
	if a != 2 {
		t.Errorf("a must be 2")
	}
	if b != 3 {
		t.Errorf("b must be 3")
	}
}
