package source_code

import (
	"testing"
)

func TestTerminal(t *testing.T) {
	cfg := &Config{}
	terminal, err := NewTerminal(cfg)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	if cfg.Painter == nil {
		cfg.Painter = &defaultPainter{}
	}

	op := terminal.Readline()
	op.SetPrompt(">> ")

	strExpected := "中a国bc"
	op.buf.WriteString(strExpected)

	// 模拟按下Enter键
	op.t.outchan <- CharEnter

	str, err := op.String()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
}
