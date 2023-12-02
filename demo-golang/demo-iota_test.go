package main

import (
	"fmt"
	"testing"
)

const (
	MyRed = iota
	MyOrange
	MyYellow
)

// https://www.gopherguides.com/articles/how-to-use-iota-in-golang
func TestIota(t *testing.T) {
	fmt.Printf("red=%d,orange=%d,yellow=%d\n", MyRed, MyOrange, MyYellow)
}
