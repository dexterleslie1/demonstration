package readline

import (
	"fmt"
	"os"
	"testing"
	"time"
)

func TestOperation(t *testing.T) {
	o := NewOperation(os.Stdout, ">> ")

	o.AddInputString("中")
	time.Sleep(500 * time.Millisecond)
	o.AddInputString("a国bc")
	time.Sleep(500 * time.Millisecond)
	// backward
	o.AddInputString("\u0002")
	time.Sleep(500 * time.Millisecond)
	o.AddInputString("\u0002")
	time.Sleep(500 * time.Millisecond)
	o.AddInputString("\u0002")
	time.Sleep(500 * time.Millisecond)
	// forward
	o.AddInputString("\u0006")
	time.Sleep(500 * time.Millisecond)
	// backspace
	o.AddInputString("\u007f")
	time.Sleep(500 * time.Millisecond)
	// enter
	o.AddInputString("\u000d")
	time.Sleep(500 * time.Millisecond)

	s, err := o.GetInput()
	if err != nil {
		t.Errorf("expected no err, got %s", err)
		return
	}

	fmt.Println("\n结果：", s)
}
