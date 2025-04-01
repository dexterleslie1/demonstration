package main

import (
	"fmt"
	"testing"
)

const (
	myString string = "Hello world!"
	myInt    int    = 19
)

// 演示定义常量
// https://blog.csdn.net/zhizhengguan/article/details/119857136

// 让编译器自动推断数据类型
const Pi = 3.14159

// 指定数据类型
const Pi1 float64 = 3.14159

// 批量定义常量
const (
	Pi2 float64 = 3.14159
	Pi3 float64 = 3.14159
)

// A1和B2使用前一个初始化值
const (
	A  = 2
	A1 // 2
	B  = 3
	B2 // 3
)

func TestConstVariable(t *testing.T) {
	if myString != "Hello world!" {
		t.Fatalf("myString must be 'Hello world!'")
	}
	if myInt != 19 {
		t.Fatalf("myInt must be 19")
	}

	if Pi != 3.14159 {
		t.Fatalf("Pi must be %f", Pi)
	}
	if Pi1 != 3.14159 {
		t.Fatalf("Pi1 must be %f", Pi1)
	}
	if Pi2 != 3.14159 {
		t.Fatalf("Pi2 must be %f", Pi2)
	}
	if Pi3 != 3.14159 {
		t.Fatalf("Pi3 must be %f", Pi3)
	}
	if A != 2 {
		t.Fatalf("A must be %d", A)
	}
	if A1 != 2 {
		t.Fatalf("A1 must be %d", A1)
	}
	if B != 3 {
		t.Fatalf("B must be %d", B)
	}
	if B2 != 3 {
		t.Fatalf("B2 must be %d", B2)
	}

	//#region 打印变量内存地址

	// https://freshman.tech/snippets/go/memory-address/
	a := 2
	fmt.Print("variable a address: ")
	fmt.Println(&a)

	fmt.Printf("variable a address: %p\n", &a)

	//#endregion
}
