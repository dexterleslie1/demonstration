package main

import (
	"bufio"
	"fmt"
	"strings"
	"testing"
)

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
