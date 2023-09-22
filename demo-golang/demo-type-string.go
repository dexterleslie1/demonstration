package main

import (
	"fmt"
	"log"
	"strconv"
)

func main() {
	// 测试string数据类型

	// 判断string长度
	myString := ""
	if len(myString) == 0 {
		fmt.Println("myString字符串为空")
	} else {
		fmt.Println("myString字符串不为空")
	}

	// 判断字符串是否空
	myString1 := " "
	if len(myString1) == 0 {
		fmt.Println("myString1字符串为空")
	} else {
		fmt.Println("myString1字符串不为空")
	}

	// string和byte[]互相转换
	// https://stackoverflow.com/questions/40632802/how-to-convert-byte-array-to-string-in-go
	// https://www.tutorialspoint.com/golang-program-to-convert-string-value-to-byte-value
	str := "我爱中国，Hello world!"
	byteArr := []byte(str)
	resultStr := string(byteArr)
	if str != resultStr {
		log.Fatal("非预期值")
	}

	// 演示string转换为int64
	str = "10000000"
	myInt64, err := strconv.ParseInt(str, 10, 64)
	if err != nil {
		log.Fatalf("调用strconv.ParseInt失败，原因: %s", err)
	}
	if myInt64 != 10000000 {
		log.Fatalf("myInt64非预期值")
	}
}
