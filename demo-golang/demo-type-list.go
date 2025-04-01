package main

import (
	"fmt"
	"log"
	"reflect"
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

	//#region 演示slice用法

	// 演示slice使用
	// https://blog.csdn.net/qq_36977923/article/details/124812086

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

	// 演示 slice[start:end] 语法
	mySlice = []int{0, 1, 2, 3, 4, 5}
	fmt.Println("mySlice[0:]=", mySlice[0:])
	fmt.Println("mySlice[:]", mySlice[:])
	fmt.Println("mySlice[1:]", mySlice[1:])
	fmt.Println("mySlice[:3+1]", mySlice[:3+1])
	fmt.Println("mySlice[3:]", mySlice[3:])

	mySlice = []int{0, 1, 2, 3, 4, 5}
	mySlice = insertBefore(mySlice, 0, 8)
	fmt.Println("mySlice在index=0位置前插入8", mySlice)
	mySlice = []int{0, 1, 2, 3, 4, 5}
	mySlice = insertBefore(mySlice, 3, 8)
	fmt.Println("mySlice在index=3位置前插入8", mySlice)
	mySlice = []int{0, 1, 2, 3, 4, 5}
	mySlice = insertBefore(mySlice, 5, 8)
	fmt.Println("mySlice在index=5位置前插入8", mySlice)

	mySlice = []int{0, 1, 2, 3, 4, 5}
	mySlice = insertAfter(mySlice, 0, 8)
	fmt.Println("mySlice在index=0位置后插入8", mySlice)
	mySlice = []int{0, 1, 2, 3, 4, 5}
	mySlice = insertAfter(mySlice, 3, 8)
	fmt.Println("mySlice在index=3位置后插入8", mySlice)
	mySlice = []int{0, 1, 2, 3, 4, 5}
	mySlice = insertAfter(mySlice, 5, 8)
	fmt.Println("mySlice在index=5位置后插入8", mySlice)

	//#endregion

	//#region 使用reflect.DeepEqual比较两个[]int是否相等

	intSlice1 := []int{1, 2, 3}
	intSlice2 := []int{1, 2, 3}
	if !reflect.DeepEqual(intSlice1, intSlice2) {
		log.Println(intSlice1, " and ", intSlice2, " is not equal")
	} else {
		log.Println(intSlice1, " and ", intSlice2, " is equal")
	}

	//#endregion
}

func insertBefore(slice []int, index int, value int) []int {
	slice = append(slice[:index+1], slice[index:]...)
	slice[index] = value
	return slice
}

func insertAfter(slice []int, index int, value int) []int {
	slice = append(slice[:index+1], slice[index:]...)
	slice[index+1] = value
	return slice
}
