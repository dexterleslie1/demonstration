package main

import (
	"bufio"
	"strings"
	"testing"
)

// https://books.studygolang.com/The-Golang-Standard-Library-by-Example/chapter01/01.4.html
func TestBufio(t *testing.T) {
	//#region ReadSlice方法使用

	// ReadSlice: 从输入中读取，直到遇到第一个界定符（delim）为止，返回一个指向缓存中字节的 slice，在下次调用读操作（read）时，这些字节会无效

	str := "http://studygolang.com. \nIt is the home of gophers\n"
	reader := bufio.NewReader(strings.NewReader(str))
	lineData, err := reader.ReadSlice('\n')
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strExpected := "http://studygolang.com. \n"
	str = string(lineData)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}

	// 再继续读取
	lineDataNew, err := reader.ReadSlice('\n')
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strExpected = "It is the home of gophers\n"
	str = string(lineDataNew)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}

	//#endregion

	//#region ReadLine方法使用

	// ReadLine: 尝试返回单独的行，不包括行尾的换行符。如果一行大于缓存，isPrefix 会被设置为 true，同时返回该行的开始部分（等于缓存大小的部分）。该行剩余的部分就会在下次调用的时候返回。当下次调用返回该行剩余部分时，isPrefix 将会是 false 。跟 ReadSlice 一样，返回的 line 只是 buffer 的引用，在下次执行IO操作时，line 会无效。

	// 测试足够缓存空间读取一行数据
	str = "http://studygolang.com. \nIt is the home of gophers"
	reader = bufio.NewReader(strings.NewReader(str))
	lineData, isPrefix, err := reader.ReadLine()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strExpected = "http://studygolang.com. "
	str = string(lineData)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
	// 缓存区有足够缓存空间读取一行数据，所以isPrefix返回false
	if isPrefix {
		t.Errorf("expected %t, got %t", false, isPrefix)
	}
	// 继续读取下一行
	lineData, isPrefix, err = reader.ReadLine()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strExpected = "It is the home of gophers"
	str = string(lineData)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
	// 缓存区有足够缓存空间读取一行数据，所以isPrefix返回false
	if isPrefix {
		t.Errorf("expected %t, got %t", false, isPrefix)
	}

	// 测试没有足够的缓存空间读取一行数据
	str = "http://studygolang.com. \nIt is the home of gophers"
	reader = bufio.NewReaderSize(strings.NewReader(str), 18)
	lineData, isPrefix, err = reader.ReadLine()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strExpected = "http://studygolang"
	str = string(lineData)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
	// 缓存区没有足够缓存空间读取一行数据，所以isPrefix返回true
	if !isPrefix {
		t.Errorf("expected %t, got %t", true, isPrefix)
	}
	// 继续读取下一行
	lineData, isPrefix, err = reader.ReadLine()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	strExpected = ".com. "
	str = string(lineData)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
	// 缓存区没有足够缓存空间读取一行数据，当时读取到一行剩余部分所以isPrefix返回false
	if isPrefix {
		t.Errorf("expected %t, got %t", false, isPrefix)
	}

	//#endregion

	//#region ReadRune方法使用

	str = "中a国bc"
	reader = bufio.NewReader(strings.NewReader(str))
	// 读取一个utf8编码的unicode字符
	r, size, err := reader.ReadRune()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	// ”中“ 字符utf8占用3个字节
	if size != 3 {
		t.Errorf("expected %d, got %d", 3, size)
	}
	if r != 20013 {
		t.Errorf("expected %d, got %d", 20013, r)
	}

	r, size, err = reader.ReadRune()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if size != 1 {
		t.Errorf("expected %d, got %d", 1, size)
	}
	if r != 97 {
		t.Errorf("expected %d, got %d", 97, r)
	}

	//#endregion

}
