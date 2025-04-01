package main

import (
	"fmt"
	"image/color"
	"os"
	"testing"

	"github.com/muesli/termenv"
)

// 演示termenv库的用法
// https://github.com/muesli/termenv

func TestBasicUsage(t *testing.T) {
	output := termenv.NewOutput(os.Stdout)

	// Returns terminal's foreground color
	color := output.ForegroundColor()
	t.Log("前景颜色: ", color)

	// Returns terminal's background color
	color = output.BackgroundColor()
	t.Log("背景颜色: ", color)

	// Returns whether terminal uses a dark-ish background
	darkTheme := output.HasDarkBackground()
	t.Log("是否使用dark背景: ", darkTheme)
}

func TestColor(t *testing.T) {
	output := termenv.NewOutput(os.Stdout)

	s := output.String("Hello World")

	// Supports hex values
	// Will automatically degrade colors on terminals not supporting RGB
	s = s.Foreground(output.Color("#abcdef"))
	// but also supports ANSI colors (0-255)
	s = s.Background(output.Color("69"))
	// ...or the color.Color interface
	s = s.Foreground(output.FromColor(color.RGBA{255, 128, 0, 255}))

	// Combine fore- & background colors
	s = s.Foreground(output.Color("#ffffff")).Background(output.Color("#0000ff"))

	// Supports the fmt.Stringer interface
	fmt.Println(s)
}

func TestStyles(t *testing.T) {
	output := termenv.NewOutput(os.Stdout)

	s := output.String("foobar")

	// Text styles
	s = s.Bold()
	s = s.Faint()
	s = s.Italic()
	s = s.CrossOut()
	s = s.Underline()
	s = s.Overline()

	// Reverse swaps current fore- & background colors
	s = s.Reverse()

	// Blinking text
	s = s.Blink()

	// Combine multiple options
	s = s.Bold().Underline()
	fmt.Println(s)
}
