package main

import "fmt"

// 演示slice使用
// https://blog.csdn.net/qq_36977923/article/details/124812086
func main() {
	// 使用make函数创建slice
	mySlice := make([]int, 0)
	fmt.Println("mySlice=", mySlice)

	// append元素到slice
	mySlice = append(mySlice, 11)
	fmt.Println("append(mySlice, 11)后的mySlice=", mySlice)

	mySlice = append(mySlice, 12, 13)
	fmt.Println("append(mySlice, 12, 13)后的mySlice=", mySlice)

	// https://stackoverflow.com/questions/16248241/concatenate-two-slices-in-go
	var mySlice1 []int
	mySlice1 = append(mySlice1, 22, 23)
	mySlice = append(mySlice, mySlice1...)
	fmt.Println("append(mySlice, mySlice1...)后的mySlice=", mySlice)
}
