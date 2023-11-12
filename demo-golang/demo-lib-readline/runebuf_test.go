package readline

import (
	"bytes"
	"testing"
)

func TestRunebuf(t *testing.T) {
	cfg := &Config{}
	err := cfg.Init()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	if cfg.Painter == nil {
		cfg.Painter = &defaultPainter{}
	}
	cfg.ForceUseInteractive = true

	buffer := bytes.NewBuffer(nil)

	runebuf := NewRuneBuffer(buffer, ">> ", cfg, cfg.FuncGetWidth())
	runebuf.WriteString("中a国bc")
	// 删除一个字符
	runebuf.Backspace()
	str := string(buffer.Bytes())
	strExpected := "\x1b[J\x1b[2K\r>> 中a国bc\x1b[J\x1b[2K\r>> 中a国b"
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
}
