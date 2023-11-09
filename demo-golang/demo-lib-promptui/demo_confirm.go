package main

import (
	"fmt"

	"github.com/manifoldco/promptui"
)

func main() {
	prompt := promptui.Prompt{
		Label:     "是否关闭系统防火墙",
		IsConfirm: true,
	}

	result, err := prompt.Run()

	if err != nil {
		fmt.Printf("Prompt failed %v\n", err)
		return
	}

	fmt.Printf("You choose %q\n", result)
}
