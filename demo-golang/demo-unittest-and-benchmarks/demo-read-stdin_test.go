package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
	"testing"
)

// 单元测试读取os.Stdin总是返回EOF
func TestReadStdin(t *testing.T) {
	buf := bufio.NewReader(os.Stdin)
	r, _, err := buf.ReadRune()
	if err != nil {
		t.Errorf("expected no err, got %s", err)
	}
	fmt.Println("你的输入：", string(r))
}

// stdin mock
func TestStdinMock(t *testing.T) {
	reader := strings.NewReader("中a国bc")
	buf := bufio.NewReader(reader)
	for {
		r, _, err := buf.ReadRune()
		if err != nil {
			break
		}
		fmt.Println("你的输入：", string(r))
	}
}
