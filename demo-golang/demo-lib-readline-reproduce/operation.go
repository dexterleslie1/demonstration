package readline

import (
	"io"
)

func NewOperation(stdout io.Writer, prompt string) *Operation {
	o := &Operation{
		rb:        NewRuneBuffer(stdout, prompt),
		outchan:   make(chan []rune),
		errchan:   make(chan error),
		inputchan: make(chan rune),
	}

	go o.ioloop()

	return o
}

type Operation struct {
	rb *RuneBuffer

	outchan chan []rune
	errchan chan error

	inputchan chan rune
}

func (o *Operation) ioloop() {
	for {
		r := o.readInput()

		if r == CharEnter {
			s := o.rb.getInput()
			o.outchan <- []rune(s)
			break
		} else if r == CharBackward {
			err := o.rb.MoveCursorBackward()
			if err != nil {
				o.errchan <- err
				break
			}
		} else if r == CharForward {
			err := o.rb.MoveCursorForward()
			if err != nil {
				o.errchan <- err
				break
			}
		} else if r == CharBackspace {
			err := o.rb.Backspace()
			if err != nil {
				o.errchan <- err
				break
			}
		} else {
			err := o.rb.WriteRune(r)
			if err != nil {
				o.errchan <- err
				break
			}
		}
	}
}

// AddInputString 添加按键对应的字符
func (o *Operation) AddInputString(s string) {
	runes := []rune(s)
	for _, r := range runes {
		o.AddInput(r)
	}
}

// AddInput 添加按键rune
func (o *Operation) AddInput(r rune) {
	o.inputchan <- r
}

// 读取输入的按键
func (o *Operation) readInput() rune {
	return <-o.inputchan
}

func (o *Operation) GetInput() (string, error) {
	select {
	case runes := <-o.outchan:
		return string(runes), nil
	case err := <-o.errchan:
		return "", err
	}
}

const (
	CharLineStart = 1
	CharBackward  = 2
	CharInterrupt = 3
	CharDelete    = 4
	CharLineEnd   = 5
	CharForward   = 6
	CharBell      = 7
	CharCtrlH     = 8
	CharTab       = 9
	CharCtrlJ     = 10
	CharKill      = 11
	CharCtrlL     = 12
	CharEnter     = 13
	CharNext      = 14
	CharPrev      = 16
	CharBckSearch = 18
	CharFwdSearch = 19
	CharTranspose = 20
	CharCtrlU     = 21
	CharCtrlW     = 23
	CharCtrlY     = 25
	CharCtrlZ     = 26
	CharEsc       = 27
	CharO         = 79
	CharEscapeEx  = 91
	CharBackspace = 127
)
