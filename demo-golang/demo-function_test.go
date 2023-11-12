// 演示golang函数
package main

import (
	"fmt"
	"strings"
	"testing"
)

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

// 测试variadic function 可变参数函数
// https://www.geeksforgeeks.org/golang-program-that-uses-func-with-variable-argument-list/
func TestVariadicFunction(t *testing.T) {
	return_arr := myVariadicFun1("Dexter", "!")
	fmt.Println(strings.Join(return_arr, " "))
}

func myVariadicFun1(args ...string) []string {
	return myVariadicFun2(args...)
}

func myVariadicFun2(args ...string) []string {
	return append([]string{"Hello"}, args...)
}

// Named return values
// https://go.dev/tour/basics/7
func TestNamedReturnValues(t *testing.T) {
	x, y := namedRetrunValues()
	fmt.Println("x=", x, ",y=", y)
}

func namedRetrunValues() (x string, y int) {
	return "hello", 36
}
