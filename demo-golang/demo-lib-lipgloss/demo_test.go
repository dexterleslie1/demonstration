package main

import (
	"fmt"
	"testing"

	"github.com/charmbracelet/lipgloss"
)

func TestBasicUsage(t *testing.T) {
	var style = lipgloss.NewStyle().
		Bold(true).
		Foreground(lipgloss.Color("#FAFAFA")).
		Background(lipgloss.Color("#7D56F4")).
		PaddingTop(2).
		PaddingLeft(4).
		Width(22)

	fmt.Println(style.Render("Hello, kitty"))
}
