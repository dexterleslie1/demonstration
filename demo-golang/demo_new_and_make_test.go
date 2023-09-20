package main

import (
	"bytes"
	"fmt"
	"sync"
	"testing"
)

type SyncedBuffer struct {
	lock   sync.Mutex
	buffer bytes.Buffer
	foo    int
	bar    string
}

// https://blog.wu-boy.com/2021/06/what-is-different-between-new-and-make-in-golang/
// https://www.cnblogs.com/xiaxiaosheng/p/11167326.html
func TestNewAndMake(t *testing.T) {
	// 可以使用new分配一个int primative类型
	myInt := new(int)
	if "0" != fmt.Sprintf("%d", *myInt) {
		t.Fatalf("myInt没有预期值")
	}

	// 可以使用new分配一个struct
	// NOTE: 但是使用new分配struct无法在分配内存同时初始化里面的属性
	mySyncedBuffer := new(SyncedBuffer)
	if 0 != mySyncedBuffer.foo {
		t.Fatalf("mySyncedBuffer.foo没有预期值")
	}
	if "" != mySyncedBuffer.bar {
		t.Fatalf("mySyncedBuffer.bar没有预期值")
	}

	// NOTE: 下面的代码会报告错误，因为初始化 map 拿到的會是 nil，故通常在宣告 slice, map 及 channel 則會使用 Go 提供的另一個宣告方式 make。
	// myMap := new(map[string]string)
	// (*myMap)["key1"] = "value1"

	// 使用make为map分配内存
	// NOTE: make 通常只用於在宣告三個地方，分別是 slice, map 及 channel
	myMap := make(map[string]string)
	myMap["key1"] = "value1"
	myMap["key2"] = "value2"
	if "value1" != myMap["key1"] {
		t.Fatalf("myMap['key1']没有预期值")
	}
	if "value2" != myMap["key2"] {
		t.Fatalf("myMap['key2']没有预期值")
	}

	// make分配slice
	mySlice := make([]int, 0)
	mySlice = append(mySlice, 11, 22)
	if 11 != mySlice[0] {
		t.Fatalf("mySlice[0]没有预期值")
	}
	if 22 != mySlice[1] {
		t.Fatalf("mySlice[1]没有预期值")
	}
}
