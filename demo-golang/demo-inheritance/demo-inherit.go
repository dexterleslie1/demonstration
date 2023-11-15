package main

import (
	"fmt"
)

// https://www.geeksforgeeks.org/inheritance-in-golang/
type Student struct {
	Name string
	Age  int
}

func (s *Student) Show() {
	fmt.Println("名称:", s.Name, "，年龄:", s.Age)
}

type Student1 struct {
	Student
}

func (s *Student1) Testing() {
	fmt.Println("Student1考试中......")
}

type Student2 struct {
	Student
}

func (s *Student2) Testing() {
	fmt.Println("Student2考试中......")
}

// 演示面向对象编程-继承
// Student1和Student2继承Student的Show方法
// Student1和Student2分别重写Student的Testing方法
func main() {
	var student1 Student1
	student1.Name = "zhangsan"
	student1.Age = 22
	var student2 Student2
	student2.Name = "lisi"
	student2.Age = 23
	student1.Testing()
	student2.Testing()
	student1.Show()
	student2.Show()

	// 构造时初始化父级field
	student1 = Student1{
		Student{
			Name: "zhangsan1",
			Age:  23,
		},
	}
	student2 = Student2{
		Student{
			Name: "lisi1",
			Age:  24,
		},
	}
	student1.Testing()
	student2.Testing()
	student1.Show()
	student2.Show()
}
