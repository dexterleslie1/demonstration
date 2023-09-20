package main

import "testing"

const (
	myString string = "Hello world!"
	myInt    int    = 19
)

func TestConstVariable(t *testing.T) {
	if myString != "Hello world!" {
		t.Fatalf("myString must be 'Hello world!'")
	}
	if myInt != 19 {
		t.Fatalf("myInt must be 19")
	}
}
