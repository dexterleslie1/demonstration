package main

import "fmt"

// 演示定义常量

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

func main() {
	fmt.Println("Pi=", Pi)
	fmt.Println("Pi1=", Pi1)
	fmt.Println("Pi2=", Pi2)
	fmt.Println("Pi3=", Pi3)
	fmt.Println("A=", A)
	fmt.Println("A1=", A1)
	fmt.Println("B=", B)
	fmt.Println("B2=", B2)
}
