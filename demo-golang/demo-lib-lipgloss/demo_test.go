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

func TestColors(t *testing.T) {
	style := lipgloss.NewStyle().Foreground(lipgloss.Color("#04B575"))
	str := style.Render("Hello world!")
	fmt.Println(str)
}

func TestBold(t *testing.T) {
	fmt.Println("没有bold文本： ", "Hello world!")

	style := lipgloss.NewStyle().Bold(true)
	fmt.Println("有bold文本： ", style.Render("Hello world!"))
}

// NOTE: blink不起作用
func TestBlink(t *testing.T) {
	fmt.Println("没有blink文本： ", "Hello world!")

	style := lipgloss.NewStyle().Blink(true)
	fmt.Println("有blink文本： ", style.Render("Hello world!"))
}

func TestBlockLevelFormatting(t *testing.T) {
	// padding
	fmt.Println("没有padding： ", "Hello world!")
	var style = lipgloss.NewStyle().
		PaddingTop(2).
		PaddingRight(4).
		PaddingBottom(2).
		PaddingLeft(4)
	fmt.Println("有padding： ", style.Render("Hello world!"))

	// margin
	fmt.Println("没有margin： ", "Hello world!")
	style = lipgloss.NewStyle().
		MarginTop(2).
		MarginRight(4).
		MarginBottom(2).
		MarginLeft(4)
	fmt.Println("有margin： ", style.Render("Hello world!"))
}

func TestWidthAndHeight(t *testing.T) {
	var style = lipgloss.NewStyle().
		SetString("What’s for lunch?").
		Width(25).
		Height(5).
		Foreground(lipgloss.Color("63"))
	fmt.Println(style.Render())
}

func TestBorders(t *testing.T) {
	// Add a purple, rectangular border
	var style = lipgloss.NewStyle().
		BorderStyle(lipgloss.NormalBorder()).
		BorderForeground(lipgloss.Color("63"))
	fmt.Println(style.Render("Hello world!"))

	// Set a rounded, yellow-on-purple border to the top and left
	var anotherStyle = lipgloss.NewStyle().
		BorderStyle(lipgloss.RoundedBorder()).
		BorderForeground(lipgloss.Color("228")).
		BorderBackground(lipgloss.Color("63")).
		BorderTop(true).
		BorderLeft(true)
	fmt.Println(anotherStyle.Render("Hello world!"))

	// Make your own border
	var myCuteBorder = lipgloss.Border{
		Top:         "._.:*:",
		Bottom:      "._.:*:",
		Left:        "|*",
		Right:       "|*",
		TopLeft:     "*",
		TopRight:    "*",
		BottomLeft:  "*",
		BottomRight: "*",
	}
	anotherStyle = lipgloss.NewStyle().Border(myCuteBorder)
	fmt.Println(anotherStyle.Render("Hello world!"))
}

func TestEnforcingRules(t *testing.T) {
	// Force rendering onto a single line, ignoring margins, padding, and borders.
	someStyle := lipgloss.NewStyle()
	fmt.Println(someStyle.Inline(true).Render("yadda yadda"))

	// Also limit rendering to five cells
	someStyle = lipgloss.NewStyle()
	fmt.Println(someStyle.Inline(true).MaxWidth(5).Render("yadda yadda"))

	// Limit rendering to a 5x5 cell block
	someStyle = lipgloss.NewStyle()
	fmt.Println(someStyle.MaxWidth(5).MaxHeight(5).Render("yadda yadda"))
}

func TestTabs(t *testing.T) {
	titleStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#04B575")).Bold(true).Underline(true)
	itemStartStyle := lipgloss.NewStyle()
	itemCorrectSignStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#00FF00")).TabWidth(4)
	itemCorrectContentStyle := lipgloss.NewStyle().MarginLeft(1)
	itemIncorrectSignStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#FF0000")).TabWidth(4)
	itemIncorrectContentStyle := lipgloss.NewStyle().MarginLeft(1).Faint(true).Strikethrough(true)
	itemEndStyle := lipgloss.NewStyle()

	fmt.Println(titleStyle.Render("配置chrony: "))
	fmt.Println(itemStartStyle.Render(""))
	fmt.Println(itemCorrectSignStyle.Render("\t*") + itemCorrectContentStyle.Render("Step one"))
	fmt.Println(itemCorrectSignStyle.Render("\t*") + itemCorrectContentStyle.Render("Step two"))
	fmt.Println(itemIncorrectSignStyle.Render("\tx") + itemIncorrectContentStyle.Render("Step three"))
	fmt.Println(itemEndStyle.Render(""))

	fmt.Println(titleStyle.Render("配置firewall: "))
	fmt.Println(itemStartStyle.Render(""))
	fmt.Println(itemCorrectSignStyle.Render("\t*") + itemCorrectContentStyle.Render("Step one"))
	fmt.Println(itemCorrectSignStyle.Render("\t*") + itemCorrectContentStyle.Render("Step two"))
	fmt.Println(itemIncorrectSignStyle.Render("\tx") + itemIncorrectContentStyle.Render("Step three"))
	fmt.Println(itemEndStyle.Render(""))
}

// func TestJoinParagraphs(t *testing.T) {
// 	// Horizontally join three paragraphs along their bottom edges
// 	paragraphA := "ppppppppppppppppppppppppp1"
// 	paragraphB := "ooooooooooooooooooooooooo2"
// 	paragraphC := "wwwwwwwwwwwwwwwwwwwwwwwww3"
// 	fmt.Println(lipgloss.JoinHorizontal(lipgloss.Bottom, paragraphA, paragraphB, paragraphC))
// }
