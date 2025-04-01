package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"syscall"
)

func main() {
	state, err := MakeRaw(syscall.Stdin)

	defer func() {
		err := restoreTerm(syscall.Stdin, state)
		if err != nil {
			// errno 0 means everything is ok :)
			if err.Error() == "errno 0" {
				log.Fatalf("%s", err)
			} else {
				log.Fatalf("%s", err)
			}
		}
	}()

	buf := bufio.NewReader(os.Stdin)
	r, _, err := buf.ReadRune()
	if err != nil {
		log.Fatalf("expected no err, got %s", err)
		return
	}
	fmt.Println("你的输入：", string(r))
}
