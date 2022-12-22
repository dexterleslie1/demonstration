package main

import "fmt"

type Person struct {
	Name string
	Age  int
}

func main() {
	// 结构体创建的四种方法
	// 方法1
	var person Person
	person.Name = "dexterleslie"
	person.Age = 22
	fmt.Println("方法1创建Person:", person)

	// 方法2
	var person1 Person = Person{}
	person1.Name = "dexterleslie1"
	person1.Age = 22
	fmt.Println("方法2创建Person:", person1)

	var person11 Person = Person{"dexterlesie11", 22}
	fmt.Println("方法2-1创建Person:", person11)

	// 方法3
	var person2 *Person = new(Person)
	person2.Name = "dexterleslie2"
	person2.Age = 22
	fmt.Println("方法3创建Person:", *person2)

	// 方法4
	var person3 *Person = &Person{}
	person3.Name = "dexterleslie3"
	person3.Age = 22
	fmt.Println("方法4创建Person:", person3)
}
