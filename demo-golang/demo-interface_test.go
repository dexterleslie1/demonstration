package main

import (
	"fmt"
	"testing"
)

type Usb interface {
	Start()
	Stop()
}

type Phone struct {
}

func (p *Phone) Start() {
	fmt.Println("手机usb开始工作")
}

func (p *Phone) Stop() {
	fmt.Println("手机usb停止工作")
}

type Camera struct {
}

func (p *Camera) Start() {
	fmt.Println("相机usb开始工作")
}

func (p *Camera) Stop() {
	fmt.Println("相机usb停止工作")
}

type Computer struct {
}

func (c *Computer) Plugin(usb Usb) {
	usb.Start()
	usb.Stop()
}

// 演示面向对象编程-接口
func main() {
	phone := &Phone{}
	camera := &Camera{}
	computer := &Computer{}
	computer.Plugin(phone)
	computer.Plugin(camera)

	result := testEmptyInterface("Hello world!")
	fmt.Printf("%s\n", result)
	testEmptyInterface(19)
}

// 演示empty interface用法
// NOTE: empty interface实质表示任意数据类型
// https://www.programiz.com/golang/empty-interface
func testEmptyInterface(param1 interface{}) interface{} {
	var a interface{} = 18
	if a == 18 {
		fmt.Println("var a interface{} == 18")
	}

	fmt.Printf("%s\n", param1)

	return "Return hello world!!!"
}

type Shape interface {
	Area() float64
}
type Color interface {
	Fill() string
}
type ColoredShape interface {
	Shape
	Color
}

type Square struct {
	Length float64
}

func (s Square) Area() float64 {
	return s.Length * s.Length
}

type Red struct {
}

func (r Red) Fill() string {
	return "red"
}

type RedSquare struct {
	Red
	Square
}

// 演示接口继承
// https://stackoverflow.com/questions/32323363/interface-inherit-from-other-interface-in-golang
// https://www.tutorialspoint.com/embedding-interfaces-in-golang
func TestInterfaceEmbedding(t *testing.T) {
	var coloredShape ColoredShape = RedSquare{Red{}, Square{Length: 5}}
	fmt.Println("Area of square: ", coloredShape.Area())
	fmt.Println("Color of square: ", coloredShape.Fill())
}

//region 演示使用interface实现方法重写(override)

func TestMethodOverride(t *testing.T) {
	var s sayHello = &sayHelloNormal{name: "Dexter"}
	helloStr := s.Say()
	if "Hello Dexter!" != helloStr {
		t.Errorf("expected 'Hello Dexter!', got %s", helloStr)
	}

	s = &sayHelloWithGreeting{sayHello: &sayHelloNormal{name: "Dexter"}, greeting: "Welcome"}
	name := s.GetName()
	if "Dexter" != name {
		t.Errorf("expected 'Dexter', got %s", name)
	}
	helloStr = s.Say()
	if "Hello Dexter ~~Welcome!" != helloStr {
		t.Errorf("expected 'Hello Dexter ~~Welcome!', got %s", helloStr)
	}
}

type sayHello interface {
	GetName() string
	Say() string
}

type sayHelloNormal struct {
	name string
}

func (s *sayHelloNormal) GetName() string {
	return s.name
}
func (s *sayHelloNormal) Say() string {
	return "Hello " + s.GetName() + "!"
}

type sayHelloWithGreeting struct {
	greeting string
	sayHello
}

// Override sayHello Say method
func (s *sayHelloWithGreeting) Say() string {
	name := s.sayHello.GetName()
	return "Hello " + name + " ~~" + s.greeting + "!"
}

//endregion
