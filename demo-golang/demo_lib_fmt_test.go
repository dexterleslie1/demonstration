package main

import (
	"errors"
	"fmt"
	"testing"
)

func TestErrorf(t *testing.T) {
	var err = errors.New("测试异常")
	err = fmt.Errorf("出现错误，原因：%v", err)
	if "*errors.errorString" != fmt.Sprintf("%T", err) {
		t.Fatalf("没有预期值")
	}
	if "出现错误，原因：测试异常" != err.Error() {
		t.Fatalf("没有预期值")
	}

	// 十六进制表示，字母形式为小写 a-f
	// https://www.geeksforgeeks.org/fmt-errorf-function-in-golang-with-examples/
	// https://blog.csdn.net/zhangkaiadl/article/details/107931333
	str := fmt.Sprintf("数值13十六进制:%x", 13)
	if "数值13十六进制:d" != str {
		t.Fatalf("没有预期值")
	}
}

// https://pkg.go.dev/fmt
func TestFormat(t *testing.T) {
	// 获取变量类型
	var myVar int = 23
	typeString := fmt.Sprintf("%T", myVar)
	if "int" != typeString {
		t.Fatalf("没有预期值")
	}

	// 打印整形变量
	myVar = 25
	myVarStr := fmt.Sprintf("%d", myVar)
	if "25" != myVarStr {
		t.Fatalf("没有预期值")
	}

	// 打印浮点型变量
	var myVarFloat float64 = 3.14
	myVarStr = fmt.Sprintf("%f", myVarFloat)
	if "3.140000" != myVarStr {
		t.Fatalf("没有预期值")
	}

	//#region 演示%v用法
	// https://blog.csdn.net/yzf279533105/article/details/105433262

	// %v只输出结构体值
	myStruct := MyStruct{
		P1: 23,
		P2: "Hello world!",
	}
	if "{23 Hello world!}" != fmt.Sprintf("%v", myStruct) {
		t.Fatalf("%s不是预期值", fmt.Sprintf("%v", myStruct))
	}

	// %+v输出结构体键和值
	myStruct = MyStruct{
		P1: 23,
		P2: "Hello world!",
	}
	if "{P1:23 P2:Hello world!}" != fmt.Sprintf("%+v", myStruct) {
		t.Fatalf("%s不是预期值", fmt.Sprintf("%+v", myStruct))
	}

	// %#v 先输出结构体名称，再输出结构体键值
	myStruct = MyStruct{
		P1: 23,
		P2: "Hello world!",
	}
	if "main.MyStruct{P1:23, P2:\"Hello world!\"}" != fmt.Sprintf("%#v", myStruct) {
		t.Fatalf("%s不是预期值", fmt.Sprintf("%#v", myStruct))
	}

	//#endregion

	//#region 格式化换行的长字符串

	// https://stackoverflow.com/questions/35142532/how-to-split-long-lines-for-fmt-sprintf
	longStr := fmt.Sprintf(`
		resource "petstore_pet" "pet" {

		}
	`)
	fmt.Println(longStr)

	//#endregion

	//#region 打印bool布尔值

	// https://stackoverflow.com/questions/7059735/how-to-print-boolean-value-in-go
	myBool := true
	fmt.Printf("myBool=%t\n", myBool)

	//#endregion
}

type MyStruct struct {
	P1 int
	P2 string
}
