package main

import (
	"errors"
)

func main() {
	// 自定义错误
	err := errors.New("自定义错误")
	if err != nil {
		panic(err)
	}
}