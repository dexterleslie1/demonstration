package main

import (
	"fmt"
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
}