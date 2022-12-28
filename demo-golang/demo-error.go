package main

import (
	"errors"
	"fmt"
)

// 带错误返回的函数
func Hello(name string) (string, error) {
	if name == "" {
		return "", errors.New("name empty")
	}

	greeting := fmt.Sprintf("Hi, %v. Welcome!", name)
	return greeting, nil
}

func main() {
	// 自定义错误
	err := errors.New("自定义错误")
	if err != nil {
		// panic(err)
		fmt.Println(err)
	}

	// 测试带错误返回的函数
	// https://go.dev/doc/tutorial/handle-errors
	greeting, err := Hello("")
	if err != nil {
		// panic(err)
		fmt.Println(err)
	}

	greeting, err = Hello("dexter")
	if err != nil {
		panic(err)
	}
	fmt.Println(greeting)
}
