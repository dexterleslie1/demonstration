package main

import (
	"fmt"
	"log"
	readline "myreadline"
)

func main() {
	rl, err := readline.NewEx(&readline.Config{})
	if err != nil {
		log.Fatalf("expected no err, got %s", err)
		return
	}

	rl.SetPrompt(">> ")
	str, err := rl.Readline()
	fmt.Println("你输入：", str)
}
