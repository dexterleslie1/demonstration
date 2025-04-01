package main

import (
	"fmt"
	"os"
	"testing"

	"golang.org/x/term"
)

func TestBasicUsage(t *testing.T) {
	// 获取term 宽高
	width, height, err := term.GetSize(int(os.Stdout.Fd()))
	if err != nil {
		t.Fatal(err)
	}

	fmt.Printf("width: %d,height: %d\n", width, height)
}
