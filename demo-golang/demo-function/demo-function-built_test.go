package main

import (
	"reflect"
	"testing"
)

// copy函数用法
// https://yourbasic.org/golang/copy-explained/
func TestCopy(t *testing.T) {
	// dst长度小于src长度
	dst := make([]int, 3)
	n := copy(dst, []int{1, 2, 3, 4, 5})
	if n != 3 {
		t.Fatalf("expected 3, got %d", n)
	}
	if !reflect.DeepEqual(dst, []int{1, 2, 3}) {
		t.Fatal("expected ", []int{1, 2, 3}, ", got ", dst)
	}

	// dst长度大于src长度
	dst = []int{1, 2, 3, 4, 5, 9}
	n = copy(dst, []int{2, 3, 4, 5, 6})
	if n != 5 {
		t.Fatalf("expected 5, got %d", n)
	}
	if !reflect.DeepEqual(dst, []int{2, 3, 4, 5, 6, 9}) {
		t.Fatal("expected ", []int{2, 3, 4, 5, 6, 9}, ", got ", dst)
	}

	// 复制自己
	dst = []int{0, 1, 2}
	n = copy(dst, dst[1:])
	if n != 2 {
		t.Fatalf("expected 2, got %d", n)
	}
	if !reflect.DeepEqual(dst, []int{1, 2, 2}) {
		t.Fatal("expected ", []int{1, 2, 2}, ", got ", dst)
	}

	// 从字符串复制
	var b = make([]byte, 5)
	n = copy(b, "Hello, world!")
	if n != 5 {
		t.Fatalf("expected 5, got %d", n)
	}
	if "Hello" != string(b) {
		t.Fatal("expected ", "Hello", ", got ", string(b))
	}
}
