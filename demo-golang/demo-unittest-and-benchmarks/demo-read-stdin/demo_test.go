package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"testing"
)

func TestReadStdin(t *testing.T) {
	fmt.Print("unittest输入任意的字符：")
	buf := bufio.NewReader(os.Stdin)
	r, _, err := buf.ReadRune()
	if err != nil {
		log.Fatalf("expected no err, got %s", err)
		return
	}
	fmt.Println("你的输入：", string(r))
}
