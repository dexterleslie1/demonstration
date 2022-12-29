package main

import "fmt"

// 演示make用法
// https://www.cnblogs.com/xiaxiaosheng/p/11167326.html
func main() {
	// make分配slice
	mySlice := make([]int, 0)
	mySlice = append(mySlice, 11, 22)
	fmt.Println("mySlice值如下:")
	for index, value := range mySlice {
		fmt.Println("\t", index, "=", value)
	}

	// make分配map
	myMap := make(map[string]string)
	myMap["k1"] = "v1"
	myMap["k2"] = "v2"
	fmt.Println("myMap=", myMap)
}
