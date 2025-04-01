package main

import (
	"fmt"
	"log"
	"strconv"
	"strings"
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

	// 演示strings.Contains
	// https://www.geeksforgeeks.org/string-contains-function-in-golang-with-examples/
	strTest := "5.15.0-46-generic"
	if strings.Contains(strTest, "el8") {
		log.Fatalf("%s不应该包含el8", strTest)
	}

	strTest = "4.18.0-373.el8.x86_64"
	if !strings.Contains(strTest, "el8") {
		log.Fatalf("%s应该包含el8", strTest)
	}

	// 演示strings.Join
	my_str_arr := []string{"Hello", "Dexter", "!"}
	if "Hello Dexter !" != strings.Join(my_str_arr, " ") {
		log.Fatalf("字符串应该为： %s", "Hello Dexter !")
	}

	// 演示string HasPrefix(startWith)
	// https://stackoverflow.com/questions/12667327/go-startswithstr-string
	strTest = "hello dexter"
	if !strings.HasPrefix(strTest, "hello") {
		log.Fatalf("字符串“%s”应该以hello开头", strTest)
	}
}
