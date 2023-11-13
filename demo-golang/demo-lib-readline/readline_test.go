package readline

import (
	"testing"
)

func TestReadline(t *testing.T) {
	rl, err := NewEx(&Config{})
	if err != nil {
		t.Fatal(err)
		return
	}

	// go func() {
	// 	for range time.Tick(time.Millisecond) {
	// 		rl.SetPrompt("hello>> ")
	// 	}
	// }()

	// go func() {
	// 	time.Sleep(100000 * time.Millisecond)
	// 	rl.Close()
	// }()

	rl.SetPrompt(">> ")
	strExpected := "中a国bc"
	// _, err = rl.Write([]byte(strExpected))
	// rl.Operation.buf.WriteString(strExpected)
	// 模拟键盘输入 “中a国bc”
	runes := []rune(strExpected)
	for _, r := range runes {
		rl.Terminal.outchan <- r
	}

	// 模拟按下Enter键
	rl.Terminal.outchan <- CharEnter

	str, err := rl.Readline()
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
}
