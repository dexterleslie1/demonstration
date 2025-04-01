package main

import (
	"testing"
	"unicode/utf8"
)

// 测试rune数据类型
// https://www.jb51.net/article/240840.htm

// rune类型是 Go 语言的一种特殊数字类型。在builtin/builtin.go文件中，它的定义：type rune = int32；官方对它的解释是：rune是类型int32的别名，在所有方面都等价于它，用来区分字符值跟整数值。使用单引号定义 ，返回采用 UTF-8 编码的 Unicode 码点。Go 语言通过rune处理中文，支持国际化多语言。
func TestDataTypeRune(t *testing.T) {
	str := "Go语言编程"

	// 使用内置函数 len() 统计字符串长度
	// 预期len(str)=14，因为“Go”占用2个字节，“语言编程”占用12个字节（中文utf8使用3个字节编码一个中文文字）
	if 14 != len(str) {
		t.Errorf("expected 14, got %d", len(str))
	}
	strBytes := []byte(str)
	if 14 != len(strBytes) {
		t.Errorf("expected 14, got %d", len(strBytes))
	}

	// 使用rune数据类型统计字符串长度
	if 6 != len([]rune(str)) {
		t.Errorf("expected 6, got %d", len([]rune(str)))
	}
	if 6 != len([]rune(string(strBytes))) {
		t.Errorf("expected 6, got %d", len([]rune(string(strBytes))))
	}
	if 6 != utf8.RuneCountInString(str) {
		t.Errorf("expected 6, got %d", utf8.RuneCountInString(str))
	}

	// 使用rune截取带中文字符串
	// 获取前四个字符“Go语言”
	if "Go语言" != string([]rune(str)[:4]) {
		t.Errorf("expected Go语言, got %s", string([]rune(str)[:4]))
	}

	// 获取单个rune
	// https://stackoverflow.com/questions/30263607/how-to-get-a-single-unicode-character-from-string
	r, _ := utf8.DecodeRuneInString("我")
	if 25105 != r {
		t.Errorf("exptected 25105, got %d", r)
	}
}
