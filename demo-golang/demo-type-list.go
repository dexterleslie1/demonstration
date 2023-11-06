package main

import (
	"fmt"
	"strconv"
	"strings"
)

// 数据类型list使用
// https://blog.csdn.net/cuit_DreamSaga/article/details/107450434
func main() {
	// 数组长度
	var arr1 = [10]int{1, 2, 3}
	fmt.Println("长度：" + strconv.Itoa(len(arr1)))

	// 打印数组
	fmt.Println(arr1)

	// 遍历数组
	for i := 0; i < len(arr1); i++ {
		fmt.Println("index=" + strconv.Itoa(i) + ",value=" + strconv.Itoa(arr1[i]))
	}

	// append和preappend
	// https://stackoverflow.com/questions/32381079/how-to-increase-array-size
	// https://medium.com/@tzuni_eh/go-append-prepend-item-into-slice-a4bf167eb7af
	my_str_arr := []string{"Hello"}
	my_str_arr = append(my_str_arr, " Dexter", "!")
	fmt.Println("append字符串：", strings.Join(my_str_arr, ""))

	my_str_arr = []string{" Dexter", "!"}
	my_str_arr = append([]string{"Hello"}, my_str_arr...)
	fmt.Println("preappend字符串：", strings.Join(my_str_arr, ""))

	// 删除最后一个元素
	// https://stackoverflow.com/questions/26172196/how-to-remove-the-last-element-from-a-slice
	my_str_arr = []string{"Hello", " Dexter", "!"}
	if len(my_str_arr) > 0 {
		my_str_arr = my_str_arr[:len(my_str_arr)-1]
	}
	fmt.Println("删除最后一个元素：", strings.Join(my_str_arr, ""))
}
