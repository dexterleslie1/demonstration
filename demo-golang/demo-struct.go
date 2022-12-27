package main

import (
	"fmt"
	"mypackage"
)

type Person struct {
	Name string
	Age  int
}

// 指定Person结构体拥有下面方法
func (p Person) showDetails() {
	fmt.Println("我的名称:", p.Name, "，年龄:", p.Age)
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

	var person12 Person = Person{
		Name: "dexterleslie12",
		Age: 23,
	}
	fmt.Println("方法2-2创建Person:", person12)

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

	// 使用工厂模式创建student实例
	var student = mypackage.NewStudent("dexterleslie5", 22)
	fmt.Println("使用工厂模式创建的student实例:", *student)

	// golang中方法是和指定的数据类型绑定的，即只能使用指定的数据类型调用方法
	// Person结构体方法
	var person5 Person
	person5.Name = "dexterleslie6"
	person5.Age = 23
	person5.showDetails()
}
