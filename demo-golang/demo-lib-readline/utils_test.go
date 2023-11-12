package readline

import (
	"testing"
	"unicode/utf8"
)

func TestWordBreak(t *testing.T) {
	r, _ := utf8.DecodeRuneInString("\t")
	if !IsWordBreak(r) {
		t.Fatalf("expected true, got %t", IsWordBreak(r))
	}

	r, _ = utf8.DecodeRuneInString("我")
	if !IsWordBreak(r) {
		t.Fatalf("expected true, got %t", IsWordBreak(r))
	}

	r, _ = utf8.DecodeRuneInString("c")
	if IsWordBreak(r) {
		t.Fatalf("expected false, got %t", IsWordBreak(r))
	}

	r, _ = utf8.DecodeRuneInString("D")
	if IsWordBreak(r) {
		t.Fatalf("expected false, got %t", IsWordBreak(r))
	}

	r, _ = utf8.DecodeRuneInString("5")
	if IsWordBreak(r) {
		t.Fatalf("expected false, got %t", IsWordBreak(r))
	}
}
