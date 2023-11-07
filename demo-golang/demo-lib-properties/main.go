package main

import (
	"fmt"
	"log"
	"os"

	"github.com/magiconair/properties"
)

func main() {
	p, err := properties.LoadFile("./test.properties", properties.UTF8)
	if err != nil {
		log.Fatalf("%s", err)
	}

	value := p.GetString("SELINUX1", "")
	fmt.Printf("SELINUX1=%s\n", value)

	value = p.GetString("SELINUX", "")
	fmt.Printf("SELINUX=%s\n", value)

	// 设置SELINUX=enforcing
	p.SetValue("SELINUX", "enforcing")
	// 设置不存在的key
	p.SetValue("K1", "V1")
	// 删除myK1
	p.Delete("myK1")

	fmt.Println("-------------")
	// 保留所有备注
	p.WriteComment(os.Stdout, "#", properties.UTF8)
	// 不保留备注
	// p.Write(os.Stdout, properties.UTF8)
}
