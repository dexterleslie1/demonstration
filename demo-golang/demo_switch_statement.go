package main

import (
	"fmt"
	"time"
)

// https://gobyexample.com/switch
func main() {
	i := 2
	fmt.Print("Write ", i, " as ")
	switch i {
	case 1:
		fmt.Println("one")
	case 2:
		fmt.Println("two")
	case 3:
		fmt.Println("three")
	}

	switch time.Now().Weekday() {
	case time.Saturday, time.Sunday:
		fmt.Println("It's the weekend")
	default:
		fmt.Println("It's a weekday")
	}

	t := time.Now()
	switch {
	case t.Hour() < 12:
		fmt.Println("It's before noon")
	default:
		fmt.Println("It's after noon")
	}

	whatAmI := func(i interface{}) {
		switch t := i.(type) {
		case bool:
			fmt.Printf("I'm a bool, value is %t\n", t)
		case int:
			fmt.Printf("I'm an int, value is %d\n", t)
			// myStruct类型
		case myStruct:
			fmt.Printf("I'm an myStruct value is %+v\n", t)
			// myStruct指针类型
		case *myStruct:
			fmt.Printf("I'm an *myStruct value is %+v\n", t)
		default:
			fmt.Printf("Don't know type %T\n", t)
		}
	}
	whatAmI(true)
	whatAmI(1)
	whatAmI("hey")
	whatAmI(myStruct{})
	whatAmI(&myStruct{})

	// fallthrough用法
	// https://www.cnblogs.com/zsy/p/6741902.html

	// fallthrough：Go里面switch默认相当于每个case最后带有break，匹配成功后不会自动向下执行其他case，而是跳出整个switch, 但是可以使用fallthrough强制执行后面的case代码。
	s := "abcd"
	switch s[1] {
	case 'a':
		fmt.Println("The integer was <= 4")
		fallthrough
	case 'b':
		fmt.Println("The integer was <= 5")
		fallthrough
	case 'c':
		fmt.Println("The integer was <= 6")
	default:
		fmt.Println("default case")
	}
}

type myStruct struct {
}
