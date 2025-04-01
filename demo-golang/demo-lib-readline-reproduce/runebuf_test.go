package readline

import (
	"fmt"
	"os"
	"testing"
	"time"
)

func TestRunebuf(t *testing.T) {
	rb := NewRuneBuffer(os.Stdout, ">> ")
	_ = rb.WriteString("中a国bc")
	time.Sleep(1 * time.Second)

	_ = rb.MoveCursorBackward()
	time.Sleep(500 * time.Millisecond)
	_ = rb.MoveCursorBackward()
	time.Sleep(500 * time.Millisecond)
	_ = rb.MoveCursorBackward()
	time.Sleep(500 * time.Millisecond)

	_ = rb.MoveCursorForward()
	time.Sleep(500 * time.Millisecond)
	_ = rb.MoveCursorForward()
	time.Sleep(500 * time.Millisecond)

	_ = rb.MoveCursorBackward()
	time.Sleep(500 * time.Millisecond)

	_ = rb.Backspace()
	time.Sleep(500 * time.Millisecond)

	fmt.Println("\n结果：", rb.getInput())
}
