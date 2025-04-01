package main

// https://www.geeksforgeeks.org/inheritance-in-golang/

import (
	"fmt"
)

// declaring first
// base struct
type first struct {

	// declaring struct variable
	base_one string
}

// declaring second
// base struct
type second struct {

	// declaring struct variable
	base_two string
}

// function to return
// first struct variable
func (f first) printBase1() string {

	// returns a string
	// of first struct
	return f.base_one
}

// function to return
// second struct variable
func (s second) printBase2() string {

	// returns a string
	// of first struct
	return s.base_two
}

// child struct which
// embeds both base structs
type child struct {

	// anonymous fields,
	// struct embedding
	// of multiple structs
	first
	second
}

// child分别多继承first和second的方法
func main() {

	// declaring an instance
	// of child struct
	c1 := child{

		// child struct can directly
		// access base struct variables
		first{
			base_one: "In base struct 1.",
		},
		second{
			base_two: "\nIn base struct 2.\n",
		},
	}

	// child struct can directly
	// access base struct methods

	// printing base method
	// using instance of child struct
	fmt.Println(c1.printBase1())
	fmt.Println(c1.printBase2())
}
