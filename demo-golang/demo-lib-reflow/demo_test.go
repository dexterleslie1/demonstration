package main

import (
	"fmt"
	"testing"

	"github.com/muesli/reflow/dedent"
	"github.com/muesli/reflow/indent"
	"github.com/muesli/reflow/padding"
	"github.com/muesli/reflow/wordwrap"
	"github.com/muesli/reflow/wrap"
)

// https://github.com/muesli/reflow

func TestWordWrapping(t *testing.T) {
	s := wordwrap.String("Hello World!", 5)
	fmt.Println(s)
}

func TestUnconditionallyWrapping(t *testing.T) {
	s := wrap.String("Hello World!", 7)
	fmt.Println(s)
}

func TestAnsiExample(t *testing.T) {
	s := wordwrap.String("I really \x1B[38;2;249;38;114mlove\x1B[0m Go!", 8)
	fmt.Println(s)
}

func TestIndentation(t *testing.T) {
	s := indent.String("Hello World!", 4)
	fmt.Println(s)
}

func TestDedentation(t *testing.T) {
	input := `    Hello World!
  Hello World!
`
	s := dedent.String(input)
	fmt.Println(s)
}

func TestPadding(t *testing.T) {
	s := padding.String("Hello", 8)
	fmt.Print(s)
	fmt.Println("World!")
}
