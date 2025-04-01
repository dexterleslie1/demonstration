package main

import (
	"fmt"
)

type person struct {
	name string
	age int
}

func NewPerson(name string, age int) *person {
	return &person{name, age}
}

func (p *person) SetName(name string) {
	p.name = name
}

func (p *person) GetName() string {
	return p.name
}

func (p *person) SetAge(age int) {
	p.age = age
}

func (p *person) GetAge() int {
	return p.age
}

func (p *person) Show() {
	fmt.Println("名称:", p.name, "，年龄:", p.age)
}

// 演示面向对象编程-封装
func main() {
	var person1 = NewPerson("dexterleslie", 32)
	person1.Show()
	person1.SetName("dexterleslie1")
	person1.SetAge(33)
	person1.Show()
}