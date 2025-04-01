package main

import (
	"log"
	"time"
)

// https://www.cnblogs.com/zhangmingcheng/p/15817344.html
func main() {
	seconds := 5
	log.Printf("%d秒后继续执行代码", seconds)
	timer := time.NewTimer(time.Duration(seconds) * time.Second)
	<-timer.C
	log.Printf("代码已执行")

	setTimeout(5, func() {
		log.Printf("演示 golang setTimeout")
	})

	// todo 暂时没有用到ticker，不研究
}

func setTimeout(seconds int, callback func()) {
	timer := time.NewTimer(time.Duration(seconds) * time.Second)
	<-timer.C
	callback()
}
