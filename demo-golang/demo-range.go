package main

import "fmt"

func main() {
	// https://studygolang.com/articles/12958

	// 遍历数组
	arr1 := []int{1, 2, 3}
	for index, value := range arr1 {
		fmt.Println(index, "=", value)
	}

	// 遍历map
	map1 := map[string]int{"k1": 1, "k2": 2}
	for key, value := range map1 {
		fmt.Println(key, "=", value)
	}
}
