package source_code

import (
	"bytes"
	"fmt"
	"os"
	"strings"
	"sync"
	"testing"
	"time"
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
	str = strings.Replace(str, "\x1b[", "@", -1)
	str = strings.Replace(str, "\r", "$", -1)
	strExpected = strings.Replace(strExpected, "\x1b[", "@", -1)
	strExpected = strings.Replace(strExpected, "\r", "$", -1)
	if strExpected != str {
		t.Errorf("expected %s, got %s", strExpected, str)
	}
}

//func TestCheckIfRunebufSupportMultipleLines(t *testing.T) {
//	cfg := &Config{}
//	err := cfg.Init()
//	if err != nil {
//		t.Fatalf("expected no err, got %s", err)
//	}
//
//	if cfg.Painter == nil {
//		cfg.Painter = &defaultPainter{}
//	}
//	cfg.ForceUseInteractive = true
//
//	runebuf := NewRuneBuffer(os.Stdout, ">> ", cfg, cfg.FuncGetWidth())
//	str := ""
//	for i := 0; i < 100; i++ {
//		str += fmt.Sprintf(" %d", i)
//	}
//	runebuf.WriteString(str)
//	runebuf.Backspace()
//}

func TestInput(t *testing.T) {
	cfg := &Config{}
	err := cfg.Init()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	if cfg.Painter == nil {
		cfg.Painter = &defaultPainter{}
	}
	cfg.ForceUseInteractive = true

	runebuf := NewRuneBuffer(os.Stdout, ">> ", cfg, cfg.FuncGetWidth())

	waitGroup := sync.WaitGroup{}
	waitGroup.Add(1)

	go func() {
		defer waitGroup.Done()

		str := "Hello dexterleslie!"
		//str := ""
		//for i := 0; i < 200; i++ {
		//	str += fmt.Sprintf(" %d", i)
		//}
		runebuf.WriteString(str)
		time.Sleep(time.Second)

		runebuf.MoveToLineStart()
		time.Sleep(5 * time.Second)
		runebuf.MoveToLineEnd()
		time.Sleep(time.Second)

		runebuf.MoveBackward()
		time.Sleep(time.Second)
		runebuf.MoveBackward()
		time.Sleep(time.Second)

		runebuf.MoveForward()
		time.Sleep(time.Second)

		runebuf.Backspace()
		time.Sleep(time.Second)

		//runebuf.Move
	}()

	waitGroup.Wait()

	fmt.Println("\n操作结果：", string(runebuf.Runes()))
}
