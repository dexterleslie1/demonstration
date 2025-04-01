package main

import (
	"fmt"
	"testing"

	"github.com/charmbracelet/glamour"
)

func TestBasicUsage(t *testing.T) {
	in := `# Hello World

This is a simple example of Markdown rendering with Glamour!
Check out the [other examples](https://github.com/charmbracelet/glamour/tree/master/examples) too.

Bye!
`

	out, _ := glamour.Render(in, "dark")
	fmt.Print(out)
}

func TestCustomRender(t *testing.T) {
	in := `# Hello World

This is a simple example of Markdown rendering with Glamour!
Check out the [other examples](https://github.com/charmbracelet/glamour/tree/master/examples) too.

Bye!
`
	r, _ := glamour.NewTermRenderer(
		// detect background color and pick either the default dark or light theme
		glamour.WithAutoStyle(),
		// wrap output at specific width (default is 80)
		glamour.WithWordWrap(40),
	)

	out, _ := r.Render(in)
	fmt.Print(out)
}
