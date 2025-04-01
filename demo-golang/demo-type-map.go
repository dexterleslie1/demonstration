package main

import "fmt"

// 演示map使用
// https://blog.csdn.net/Destiny_shine/article/details/118086214
func main() {
	// 使用make初始化
	myMap := make(map[string]string)
	myMap["k1"] = "v1"
	myMap["k2"] = "v2"
	fmt.Println("myMap内容如下:")
	for key, value := range myMap {
		fmt.Println("\t", key, "=", value)
	}

	// 在声明时初始化
	myMap1 := map[string]string{
		"kk1": "vv1",
		"kk2": "vv2",
	}
	fmt.Println("myMap1内容如下:")
	for key, value := range myMap1 {
		fmt.Println("\t", key, "=", value)
	}
}
