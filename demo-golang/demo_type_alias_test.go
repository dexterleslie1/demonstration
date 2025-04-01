// 演示数据类型别名
// https://gosolve.io/golang-type-alias/

package main

import (
	"fmt"
	"testing"
)

// 普通数据类型别名
type MyIntType = int
type MyIntType2 int

func TestOrdinaryTypeAlias(t *testing.T) {
	var myInt1 MyIntType = 1
	var myInt2 MyIntType2 = 2
	if "int" != fmt.Sprintf("%T", myInt1) {
		t.Fatalf("myInt1 must be in type int")
	}
	if "main.MyIntType2" != fmt.Sprintf("%T", myInt2) {
		t.Fatalf("myInt2 must be int type int")
	}
}

// 函数数据类型别名
type MyFunc = func(name string, age int) (string, error)

func TestFuncTypeAlias(t *testing.T) {
	str := MyFuncWithFuncRefParam(func(name string, age int) (string, error) {
		return fmt.Sprintf("Hello %s, You are %d", name, age), nil
	})

	if "Hello Dexter, You are 23" != str {
		t.Fatalf("Result %s is not match expected", str)
	}
}
func MyFuncWithFuncRefParam(myFunc MyFunc) string {
	str, _ := myFunc("Dexter", 23)
	return str
}
