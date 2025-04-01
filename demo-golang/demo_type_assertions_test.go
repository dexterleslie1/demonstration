// 演示type assertions用法
// https://go.dev/tour/methods/15
// https://stackoverflow.com/questions/14289256/cannot-convert-data-type-interface-to-type-string-need-type-assertion

package main

import (
	"fmt"
	"testing"
)

func TestTypeAssertions(t *testing.T) {
	var i interface{} = "hello"

	s := i.(string)
	fmt.Println(s)

	s, ok := i.(string)
	fmt.Println(s, ok)

	f, ok := i.(float64)
	fmt.Println(f, ok)

	// f = i.(float64) // panic
	// fmt.Println(f)
}
