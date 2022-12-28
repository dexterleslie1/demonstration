package main

import (
	"fmt"
	"strconv"
)

// 数据类型list使用
// https://blog.csdn.net/cuit_DreamSaga/article/details/107450434
func main() {
	// 数组长度
	var arr1 = [10] int {1, 2, 3}
	fmt.Println("长度：" + strconv.Itoa(len(arr1)))

	// 打印数组
	fmt.Println(arr1)

	// 遍历数组
	for i:=0; i<len(arr1); i++ {
		fmt.Println("index=" + strconv.Itoa(i) + ",value=" + strconv.Itoa(arr1[i]))
	}
}
