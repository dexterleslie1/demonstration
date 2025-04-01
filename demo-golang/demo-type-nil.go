package main

import "fmt"

func main() {
	myVar := GetNil()
	fmt.Printf("nil length: %d\n", len(myVar))
}

func GetNil() []string {
	return nil
}
