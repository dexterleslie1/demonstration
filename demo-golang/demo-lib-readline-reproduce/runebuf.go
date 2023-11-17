package readline

import (
	"bytes"
	"fmt"
	"io"
)

// NewRuneBuffer 创建RuneBuffer
func NewRuneBuffer(targetWriter io.Writer, prompt string) *RuneBuffer {
	return &RuneBuffer{
		targetWriter: targetWriter,
		prompt:       []rune(prompt),
	}
}

type RuneBuffer struct {
	// 用户输入内容
	buffer []rune
	// 当前光标位置
	cursorPosition int
	// 输入提示
	prompt []rune

	// view输出目标
	targetWriter io.Writer
}

// SetPrompt 设置输入提示，例如 SetPrompt(">> ")后，命令行会显示 >> 提示用户输入
func (rb *RuneBuffer) SetPrompt(prompt string) {
	rb.prompt = []rune(prompt)
}

// WriteString 写入字符串到缓存中
func (rb *RuneBuffer) WriteString(s string) error {
	err := rb.WriteRunes([]rune(s))
	if err != nil {
		return err
	}

	return nil
}

// WriteRune 写入一个rune数据到缓存中
func (rb *RuneBuffer) WriteRune(r rune) error {
	return rb.WriteRunes([]rune{r})
}

// WriteRunes 写入 []rune 到缓存中
func (rb *RuneBuffer) WriteRunes(runes []rune) error {
	if len(runes) > 0 {
		tail := append(runes, rb.buffer[rb.cursorPosition:]...)
		rb.buffer = append(rb.buffer[:rb.cursorPosition], tail...)
		rb.cursorPosition = rb.cursorPosition + len(runes)
	}

	err := rb.print()
	if err != nil {
		return err
	}

	return nil
}

// MoveCursorBackward 光标向左移动一个字符
func (rb *RuneBuffer) MoveCursorBackward() error {
	if rb.cursorPosition > 0 {
		rb.cursorPosition = rb.cursorPosition - 1
	}

	err := rb.print()
	if err != nil {
		return err
	}

	return nil
}

// MoveCursorForward 光标向右移动一个字符
func (rb *RuneBuffer) MoveCursorForward() error {
	if rb.cursorPosition < len(rb.buffer) {
		rb.cursorPosition = rb.cursorPosition + 1
	}

	err := rb.print()
	if err != nil {
		return err
	}

	return nil
}

// Backspace 删除光标前的一个字符
func (rb *RuneBuffer) Backspace() error {
	if rb.cursorPosition > 0 {
		rb.cursorPosition -= 1
		rb.buffer = append(rb.buffer[:rb.cursorPosition], rb.buffer[rb.cursorPosition+1:]...)
	}

	err := rb.print()
	if err != nil {
		return err
	}

	return nil
}

// 获取用户输入内容
func (rb *RuneBuffer) getInput() string {
	return string(rb.buffer)
}

func (rb *RuneBuffer) view() ([]byte, error) {
	buf := bytes.NewBuffer(nil)

	// 输入提示
	if len(rb.prompt) > 0 {
		_, err := buf.WriteString(string(rb.prompt))
		if err != nil {
			return nil, err
		}
	}

	// 用户输入内容
	if len(rb.buffer) > 0 {
		for _, r := range rb.buffer {
			_, err := buf.WriteRune(r)
			if err != nil {
				return nil, err
			}
		}
	}

	// 根据cursorPosition定位光标位置
	if len(rb.buffer) > rb.cursorPosition {
		controlCodes := rb.getCursorPositionControlCodes()
		if len(controlCodes) > 0 {
			_, err := buf.Write([]byte(controlCodes))
			if err != nil {
				return nil, err
			}
		}
	}

	return buf.Bytes(), nil
}

func (rb *RuneBuffer) getCursorPositionControlCodes() string {
	controlCodes := ""
	// 定位光标到行首
	controlCodes = controlCodes + "\r"
	// 光标向右移动
	controlCodes = controlCodes + fmt.Sprintf("\033[%dC", len(rb.prompt)+rb.cursorPosition)

	return controlCodes
}

// 写入view数据到目标writer中
func (rb *RuneBuffer) print() error {
	err := rb.cleanPrinted()
	if err != nil {
		return err
	}

	viewData, err := rb.view()
	if err != nil {
		return err
	}

	// 输出view数据到目标writer
	if len(viewData) > 0 {
		_, err := rb.targetWriter.Write(viewData)
		if err != nil {
			return err
		}
	}

	return nil
}

// 清除已经输出的view数据
func (rb *RuneBuffer) cleanPrinted() error {
	// 清除光标到屏幕结束处显示内容
	_, err := rb.targetWriter.Write([]byte("\033[0J"))
	if err != nil {
		return err
	}

	// 清除光标处整行内容
	_, err = rb.targetWriter.Write([]byte("\033[2K"))
	if err != nil {
		return err
	}

	// 光标回到行首
	_, err = rb.targetWriter.Write([]byte("\r"))
	if err != nil {
		return err
	}

	return nil
}
