package main

import (
	"fmt"
	"testing"
	"unsafe"
)

type Person struct {
	Name string
	Age  int
}

// 指定Person结构体拥有下面方法
func (p Person) showDetails() {
	fmt.Println("我的名称:", p.Name, "，年龄:", p.Age)
}

func newPerson(name string, age int) *Person {
	return &Person{name, age}
}

func TestStructBasic(t *testing.T) {
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
		Age:  23,
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

	// 使用工厂模式创建person实例
	myPerson := newPerson("dexterleslie5", 22)
	fmt.Println("使用工厂模式创建的person实例:", *myPerson)

	// golang中方法是和指定的数据类型绑定的，即只能使用指定的数据类型调用方法
	// Person结构体方法
	var person5 Person
	person5.Name = "dexterleslie6"
	person5.Age = 23
	person5.showDetails()

	//#region 空结构体用法

	// https://geektutu.com/post/hpg-empty-struct.html

	// 空结构体 struct{} 实例不占据任何的内存空间
	myStruct := struct{}{}
	length := unsafe.Sizeof(myStruct)
	fmt.Printf("空结构体struct{}长度为: %d\n", length)

	// 使用空结构体实现集合(Set)
	// Go 语言标准库没有提供 Set 的实现，通常使用 map 来代替。事实上，对于集合来说，只需要 map 的键，而不需要值。即使是将值设置为 bool 类型，也会多占据 1 个字节，那假设 map 中有一百万条数据，就会浪费 1MB 的空间。
	// 因此呢，将 map 作为集合(Set)使用时，可以将值类型定义为空结构体，仅作为占位符使用即可。
	fmt.Println("\n---------- 使用空结构体实现集合(Set) ----------------")
	s := make(Set)
	s.Add("Tom")
	s.Add("Sam")
	fmt.Println(s.Has("Tom"))
	fmt.Println(s.Has("Jack"))

	// 在部分场景下，结构体只包含方法，不包含任何的字段。例如上面例子中的 Door，在这种情况下，Door 事实上可以用任何的数据结构替代。例如：
	// type Door int、type Door bool 无论是 int 还是 bool 都会浪费额外的内存，因此呢，这种情况下，声明为空结构体是最合适的
	fmt.Println("\n---------- 仅包含方法的结构体 ----------------")
	door := Door{}
	door.Open()
	door.Close()

	//#endregion

	//region 演示for range修改struct陷阱

	fmt.Println("\n---------- 演示for range修改struct陷阱 ----------------")
	persons := []Person{
		{
			Name: "p1",
			Age:  11,
		},
		{
			Name: "p2",
			Age:  12,
		},
	}
	fmt.Println("修改前persons=", persons)

	for i := range persons {
		// NOTE: 下面写法是不能修改persons数组中的值，因为p变量是persons中元素的复制
		//p.Name = p.Name + "1"
		//p.Age = p.Age + 1

		// NOTE: 下面写法是不能修改persons数组中的值
		//p := persons[i]
		//p.Name = p.Name + "1"
		//p.Age = p.Age + 1

		// 正确写法
		//persons[i].Name = p.Name + "1"
		//persons[i].Age = p.Age + 1

		// 正确写法
		p := &persons[i]
		p.Name = p.Name + "1"
		p.Age = p.Age + 1
	}
	fmt.Println("修改后persons=", persons)

	//endregion
}

type Set map[string]struct{}

func (s Set) Has(key string) bool {
	_, ok := s[key]
	return ok
}

func (s Set) Add(key string) {
	s[key] = struct{}{}
}

func (s Set) Delete(key string) {
	delete(s, key)
}

type Door struct{}

func (d Door) Open() {
	fmt.Println("Open the door")
}

func (d Door) Close() {
	fmt.Println("Close the door")
}
