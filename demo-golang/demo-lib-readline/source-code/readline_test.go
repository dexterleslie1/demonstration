package source_code

import (
	"io"
	"strings"
	"testing"
)

func TestReadline(t *testing.T) {
	strExpected := "中a国bc"
	reader := strings.NewReader(strExpected)
	readerCloser := io.NopCloser(reader)
	rl, err := NewEx(&Config{Stdin: readerCloser})
	if err != nil {
		t.Fatal(err)
		return
	}

	rl.SetPrompt(">> ")

	str, err := rl.Readline()
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
}

func TestReadlineWithControlCharacter(t *testing.T) {
	str := "中a国bc\u0002\u0002\u007F"
	reader := strings.NewReader(str)
	readerCloser := io.NopCloser(reader)
	rl, err := NewEx(&Config{Stdin: readerCloser})
	if err != nil {
		t.Fatal(err)
		return
	}

	strExpected := "中abc"

	rl.SetPrompt(">> ")

	str, err = rl.Readline()
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
}
